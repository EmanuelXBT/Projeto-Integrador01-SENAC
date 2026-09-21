package br.com.qawler.service;

import br.com.qawler.dto.SessaoResponse;
import br.com.qawler.entity.Sistema;
import br.com.qawler.repository.SistemaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Login assistido (especificação: docs/LOGIN-ASSISTIDO.md).
 *
 * <p>Abre/registra a janela do navegador <b>na máquina do usuário</b> — a <i>estação de
 * autenticação</i> — e permite que o crawler anexe a essa sessão via CDP
 * ({@code debuggerAddress}), já autenticada pelo próprio usuário.</p>
 *
 * <ul>
 *   <li><b>LOCAL (M1)</b> — a janela abre na máquina onde a aplicação roda
 *       ({@code estacao} vazio/&quot;localhost&quot;).</li>
 *   <li><b>REMOTO (M2)</b> — a janela é aberta na estação pelo helper {@code qawler-login};
 *       a aplicação apenas valida e anexa ao endereço informado ({@code host:porta}).</li>
 * </ul>
 *
 * <p><b>Regras:</b> nenhuma credencial é armazenada; a porta de debug pertence à rede
 * privada (tailnet/LAN — nunca internet) e o perfil é <b>dedicado</b>
 * ({@code ~/.qawler/chrome-profile}), nunca o perfil pessoal do usuário.
 * Numa sessão anexada o driver <b>não</b> pode ser fechado com {@code quit()} — isso
 * fecharia a janela da pessoa.</p>
 */
@Service
public class SessaoAssistidaService {

    private static final Logger log = LoggerFactory.getLogger(SessaoAssistidaService.class);

    /** Endereços que significam "a máquina onde a aplicação roda". */
    private static final List<String> LOCAIS = List.of("", "local", "localhost", "127.0.0.1", "::1", "0.0.0.0");

    private final SistemaRepository sistemaRepository;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private final boolean habilitado;
    private final Path perfil;
    private final int portaPadrao;
    private final String binarioConfigurado;
    private final int timeoutConexaoMs;

    /**
     * Estado em memória. A janela vive <b>fora</b> da aplicação: se a aplicação reiniciar,
     * a janela continua aberta e pode ser adotada com {@link #anexar(Long, String)}.
     */
    private final Map<Long, Sessao> sessoes = new ConcurrentHashMap<>();

    public SessaoAssistidaService(SistemaRepository sistemaRepository,
                                  @Value("${crawler.assistido.habilitado:true}") boolean habilitado,
                                  @Value("${crawler.assistido.perfil:}") String perfil,
                                  @Value("${crawler.assistido.porta:9222}") int portaPadrao,
                                  @Value("${crawler.assistido.binario:}") String binarioConfigurado,
                                  @Value("${crawler.assistido.timeout-conexao-ms:8000}") int timeoutConexaoMs) {
        this.sistemaRepository = sistemaRepository;
        this.habilitado = habilitado;
        this.perfil = (perfil == null || perfil.isBlank())
                ? Path.of(System.getProperty("user.home", "."), ".qawler", "chrome-profile")
                : Path.of(perfil);
        this.portaPadrao = portaPadrao;
        this.binarioConfigurado = binarioConfigurado == null ? "" : binarioConfigurado.trim();
        this.timeoutConexaoMs = timeoutConexaoMs;
    }

    // ── API ──────────────────────────────────────────────

    /** Abre a janela (M1) ou registra a estação (M2) conforme o endereço informado. */
    public SessaoResponse abrir(Long sistemaId, String estacao) {
        return registrar(sistemaId, estacao, true);
    }

    /** Adota uma janela já aberta (aplicação reiniciada, ou helper da estação já em execução). */
    public SessaoResponse anexar(Long sistemaId, String estacao) {
        return registrar(sistemaId, estacao, false);
    }

    /** Estado da sessão do sistema, se houver. */
    public Optional<SessaoResponse> consultar(Long sistemaId) {
        Sessao s = sessoes.get(sistemaId);
        return s == null ? Optional.empty() : Optional.of(descrever(s, null));
    }

    /** Marca o login como concluído pelo usuário (“Concluí o login”). */
    public SessaoResponse confirmar(Long sistemaId) {
        Sessao s = exigir(sistemaId);
        Sessao confirmada = new Sessao(s.sistemaId(), s.modo(), s.host(), s.porta(),
                s.urlAlvo(), s.abertaEm(), false, s.processo());
        sessoes.put(sistemaId, confirmada);
        log.info("Login assistido confirmado — sistemaId={} (aguardandoLogin=false)", sistemaId);
        return descrever(confirmada, "login confirmado pelo usuário — pronto para a varredura autenticada");
    }

    /** Remove a sessão do registro. A janela do usuário não é fechada à força. */
    public void encerrar(Long sistemaId) {
        Sessao s = sessoes.remove(sistemaId);
        if (s == null) {
            return;
        }
        if (s.processo() != null) {
            finalizarProcesso(s.processo());
        }
        log.info("Sessão assistida removida do registro — sistemaId={}, modo={}", sistemaId, s.modo());
    }

    /**
     * Encerra a árvore do processo que <b>esta aplicação</b> iniciou. O Chrome real se
     * destaca em um processo próprio, então isto normalmente libera apenas o lançador — a
     * janela do usuário continua aberta (fechada pela pessoa ou pelo helper, com --stop).
     */
    private void finalizarProcesso(Process processo) {
        if (processo == null) {
            return;
        }
        processo.descendants().forEach(ProcessHandle::destroy);
        if (processo.isAlive()) {
            processo.destroy();
        }
    }

    /** Verifica se o endereço da estação responde (botão “Testar conexão”). */
    public Map<String, Object> testar(String estacao) {
        Map<String, Object> resposta = new LinkedHashMap<>();
        try {
            Endereco e = parse(estacao);
            JsonNode versao = json("http://" + e.host() + ":" + e.porta() + "/json/version");
            resposta.put("ok", versao != null);
            resposta.put("endereco", e.host() + ":" + e.porta());
            resposta.put("modo", e.local() ? "LOCAL" : "REMOTO");
            resposta.put("navegador", versao == null ? null : texto(versao, "Browser"));
            if (versao == null) {
                resposta.put("erro", "sem resposta em /json/version — a janela está aberta e a porta acessível? "
                        + "(use o IP da estação, não o nome DNS)");
            }
        } catch (IllegalArgumentException ex) {
            resposta.put("ok", false);
            resposta.put("erro", ex.getMessage());
        }
        return resposta;
    }

    /**
     * Devolve um {@link WebDriver} <b>anexado</b> à janela do usuário (CDP).
     *
     * <p><b>Atenção:</b> não chamar {@code quit()} neste driver — encerraria a janela da
     * pessoa. O chamador deve apenas abandonar a referência ao terminar a varredura.</p>
     */
    public WebDriver anexarDriver(Long sistemaId) {
        Sessao s = exigir(sistemaId);
        String endereco = s.host() + ":" + s.porta();
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("debuggerAddress", endereco);
        log.info("Anexando Selenium à sessão assistida — sistemaId={}, debuggerAddress={}", sistemaId, endereco);
        return new ChromeDriver(options);
    }

    // ── Interno ──────────────────────────────────────────

    private SessaoResponse registrar(Long sistemaId, String estacao, boolean abrirJanela) {
        if (!habilitado) {
            throw new IllegalStateException("login assistido desabilitado (crawler.assistido.habilitado=false)");
        }
        Sistema sistema = sistemaRepository.findById(sistemaId)
                .orElseThrow(() -> new IllegalArgumentException("sistema " + sistemaId + " não encontrado"));
        String url = sistema.getUrlBase();
        validarUrl(url);

        Endereco e = parse(estacao);
        Process processo = null;
        String observacao;

        if (e.local()) {
            if (abrirJanela) {
                processo = abrirNavegadorLocal(e.porta(), url);
                observacao = "janela aberta na máquina da aplicação (M1) · perfil dedicado: " + perfil;
            } else {
                observacao = "sessão local adotada — nenhuma janela foi aberta por esta aplicação";
            }
        } else if (abrirJanela) {
            observacao = "modo remoto (M2): abra a janela NA ESTAÇÃO com o helper qawler-login "
                    + "(estação " + e.host() + ":" + e.porta() + ") antes de confirmar o login";
        } else {
            observacao = "sessão remota adotada (estação " + e.host() + ":" + e.porta() + ")";
        }

        Sessao anterior = sessoes.get(sistemaId);
        if (anterior != null) {
            // Trocar de modo/estação com uma janela já lançada por nós não pode deixar
            // processo órfão: encerramos o que esta aplicação iniciou antes de registrar.
            finalizarProcesso(anterior.processo());
            log.info("Sessão anterior do sistema {} encerrada antes de registrar a nova (modo {} -> {})",
                    sistemaId, anterior.modo(), e.local() ? "LOCAL" : "REMOTO");
        }

        Sessao sessao = new Sessao(sistemaId, e.local() ? "LOCAL" : "REMOTO", e.host(), e.porta(),
                url, Instant.now(), true, processo);
        sessoes.put(sistemaId, sessao);
        log.info("Sessão assistida registrada — sistemaId={}, modo={}, estacao={}:{}",
                sistemaId, sessao.modo(), sessao.host(), sessao.porta());
        return descrever(sessao, observacao);
    }

    private SessaoResponse descrever(Sessao s, String observacao) {
        String base = "http://" + s.host() + ":" + s.porta();
        JsonNode versao = json(base + "/json/version");
        boolean conectada = versao != null;
        String obs = observacao;
        if (obs == null && !conectada) {
            obs = "sem resposta em /json/version — janela fechada, porta inacessível ou endereço incorreto";
        }
        return new SessaoResponse(
                s.sistemaId(), true, conectada, s.aguardandoLogin(), s.modo(),
                s.host() + ":" + s.porta(), s.host() + ":" + s.porta(), s.urlAlvo(),
                conectada ? urlDaPrimeiraPagina(base) : null,
                conectada ? texto(versao, "Browser") : null,
                s.abertaEm(), obs);
    }

    private Sessao exigir(Long sistemaId) {
        Sessao s = sessoes.get(sistemaId);
        if (s == null) {
            throw new IllegalStateException("nenhuma sessão assistida registrada para o sistema " + sistemaId
                    + " — use POST /api/sistemas/" + sistemaId + "/sessao/abrir (ou /anexar)");
        }
        return s;
    }

    private Process abrirNavegadorLocal(int porta, String url) {
        String binario = binario();
        if (binario == null) {
            throw new IllegalStateException("navegador não encontrado nesta máquina — instale o Google Chrome "
                    + "ou defina crawler.assistido.binario (QAWLER_CHROME_BIN)");
        }
        try {
            Files.createDirectories(perfil);
        } catch (IOException ex) {
            throw new IllegalStateException("não foi possível criar o perfil dedicado " + perfil + ": " + ex.getMessage(), ex);
        }

        List<String> comando = new ArrayList<>();
        comando.add(binario);
        comando.add("--remote-debugging-port=" + porta);
        comando.add("--remote-debugging-address=127.0.0.1");
        comando.add("--user-data-dir=" + perfil.toAbsolutePath());
        comando.add("--no-first-run");
        comando.add("--no-default-browser-check");
        comando.add(url);

        try {
            Process p = new ProcessBuilder(comando)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .redirectErrorStream(true)
                    .start();
            log.info("Navegador iniciado para login assistido (pid={}) — porta CDP {}", p.pid(), porta);
            aguardarCdp("127.0.0.1", porta, 2500);
            return p;
        } catch (IOException ex) {
            throw new IllegalStateException("falha ao abrir o navegador (" + binario + "): " + ex.getMessage(), ex);
        }
    }

    /** Espera (curta) o endpoint CDP responder; não bloqueia o fluxo se a janela demorar. */
    private void aguardarCdp(String host, int porta, long ms) {
        long limite = System.currentTimeMillis() + ms;
        while (System.currentTimeMillis() < limite) {
            if (json("http://" + host + ":" + porta + "/json/version") != null) {
                return;
            }
            try {
                Thread.sleep(200);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    /** Detecta o navegador do sistema — portabilidade: macOS, Windows e Linux. */
    private String binario() {
        if (!binarioConfigurado.isBlank()) {
            return binarioConfigurado;
        }
        String os = System.getProperty("os.name", "").toLowerCase();
        List<String> candidatos = new ArrayList<>();

        if (os.contains("mac")) {
            candidatos.add("/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            candidatos.add(System.getProperty("user.home", "") + "/Applications/Google Chrome.app/Contents/MacOS/Google Chrome");
            candidatos.add("/Applications/Chromium.app/Contents/MacOS/Chromium");
        } else if (os.contains("win")) {
            String pf = System.getenv().getOrDefault("PROGRAMFILES", "C:\\Program Files");
            String pf86 = System.getenv().getOrDefault("PROGRAMFILES(X86)", "C:\\Program Files (x86)");
            String local = System.getenv().getOrDefault("LOCALAPPDATA", "");
            candidatos.add(pf + "\\Google\\Chrome\\Application\\chrome.exe");
            candidatos.add(pf86 + "\\Google\\Chrome\\Application\\chrome.exe");
            candidatos.add(local + "\\Google\\Chrome\\Application\\chrome.exe");
        } else {
            candidatos.addAll(List.of("/usr/bin/google-chrome", "/usr/bin/google-chrome-stable",
                    "/usr/bin/chromium", "/usr/bin/chromium-browser", "/snap/bin/chromium"));
            String path = System.getenv("PATH");
            if (path != null) {
                for (String dir : path.split(File.pathSeparator)) {
                    for (String nome : List.of("google-chrome", "google-chrome-stable", "chromium", "chromium-browser")) {
                        candidatos.add(dir + File.separator + nome);
                    }
                }
            }
        }

        for (String candidato : candidatos) {
            if (candidato == null || candidato.isBlank()) {
                continue;
            }
            File arquivo = new File(candidato);
            if (arquivo.isFile() && arquivo.canExecute()) {
                return arquivo.getAbsolutePath();
            }
        }
        return null;
    }

    private JsonNode json(String url) {
        try {
            HttpRequest requisicao = HttpRequest.newBuilder(URI.create(url))
                    .timeout(Duration.ofMillis(timeoutConexaoMs))
                    .GET()
                    .build();
            HttpResponse<String> resposta = http.send(requisicao, HttpResponse.BodyHandlers.ofString());
            if (resposta.statusCode() != 200) {
                return null;
            }
            return mapper.readTree(resposta.body());
        } catch (Exception ex) {
            return null;
        }
    }

    /** URL da primeira aba real da janela (via {@code /json/list}). */
    private String urlDaPrimeiraPagina(String base) {
        JsonNode lista = json(base + "/json/list");
        if (lista == null || !lista.isArray()) {
            return null;
        }
        for (JsonNode alvo : lista) {
            String tipo = texto(alvo, "type");
            String url = texto(alvo, "url");
            if ("page".equals(tipo) && url != null && !url.isBlank()
                    && !url.startsWith("devtools://") && !url.startsWith("chrome://") && !url.startsWith("about:")) {
                return url;
            }
        }
        return null;
    }

    private static String texto(JsonNode node, String campo) {
        if (node == null) {
            return null;
        }
        JsonNode valor = node.get(campo);
        return valor == null || valor.isNull() ? null : valor.asText();
    }

    private static void validarUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("o sistema não tem URL base cadastrada");
        }
        try {
            URI u = URI.create(url);
            boolean esquemaOk = "http".equals(u.getScheme()) || "https".equals(u.getScheme());
            if (u.getHost() == null || !esquemaOk) {
                throw new IllegalArgumentException("URL base inválida: " + url);
            }
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("URL base inválida: " + url);
        }
    }

    /** vazio/localhost = máquina local (M1); outro host = estação remota (M2). */
    private Endereco parse(String estacao) {
        String valor = estacao == null ? "" : estacao.trim();
        if (LOCAIS.contains(valor.toLowerCase())) {
            return new Endereco("127.0.0.1", portaPadrao, true);
        }
        String host = valor;
        int porta = portaPadrao;
        int idx = valor.lastIndexOf(':');
        if (idx > 0 && idx < valor.length() - 1 && !valor.endsWith("]")) {
            host = valor.substring(0, idx).replace("[", "").replace("]", "");
            try {
                porta = Integer.parseInt(valor.substring(idx + 1));
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException("porta inválida em \"" + estacao
                        + "\" — use host:porta (ex.: 100.81.89.63:9222)");
            }
        }
        if (host.isBlank()) {
            throw new IllegalArgumentException("endereço da estação vazio");
        }
        boolean local = LOCAIS.contains(host.toLowerCase());
        return new Endereco(local ? "127.0.0.1" : host, porta, local);
    }

    private record Endereco(String host, int porta, boolean local) {
    }

    private record Sessao(Long sistemaId, String modo, String host, int porta, String urlAlvo,
                          Instant abertaEm, boolean aguardandoLogin, Process processo) {
    }
}
