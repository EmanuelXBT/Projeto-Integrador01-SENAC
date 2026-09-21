package br.com.qawler.controller;

import br.com.qawler.entity.Bug;
import br.com.qawler.entity.Sistema;
import br.com.qawler.entity.Teste;
import br.com.qawler.enums.StatusTeste;
import br.com.qawler.enums.TipoBug;
import br.com.qawler.repository.BugRepository;
import br.com.qawler.repository.SistemaRepository;
import br.com.qawler.repository.TesteRepository;
import br.com.qawler.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Camada web (telas Thymeleaf): login no navegador, dashboard, sistemas e detalhe do teste.
 *
 * <p>O login grava o JWT em cookie <b>HttpOnly</b> + <b>SameSite=Strict</b>; o
 * {@link br.com.qawler.config.JwtAuthFilter} aceita cookie <i>ou</i> cabeçalho
 * {@code Authorization: Bearer}, então a API REST continua stateless para clientes.
 */
@Controller
public class WebController {

    /** Nome do cookie com o token da interface web (mesma constante usada pelo filtro). */
    public static final String COOKIE_TOKEN = br.com.qawler.config.AuthCookie.NOME;
    private static final int COOKIE_MAX_AGE = 8 * 60 * 60;
    private static final DateTimeFormatter DATA_HORA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final Logger log = LoggerFactory.getLogger(WebController.class);

    private final AuthService authService;
    private final SistemaRepository sistemaRepository;
    private final TesteRepository testeRepository;
    private final BugRepository bugRepository;

    public WebController(AuthService authService,
                         SistemaRepository sistemaRepository,
                         TesteRepository testeRepository,
                         BugRepository bugRepository) {
        this.authService = authService;
        this.sistemaRepository = sistemaRepository;
        this.testeRepository = testeRepository;
        this.bugRepository = bugRepository;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/dashboard";
    }

    @GetMapping("/login")
    public String telaLogin() {
        return "login";
    }

    /** Autentica e grava o token em cookie para as telas. */
    @PostMapping("/login")
    public String autenticar(@RequestParam String email,
                             @RequestParam String password,
                             HttpServletResponse response,
                             Model model) {
        try {
            String token = authService.login(email, password);
            Cookie cookie = new Cookie(COOKIE_TOKEN, token);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setMaxAge(COOKIE_MAX_AGE);
            cookie.setAttribute("SameSite", "Strict");
            response.addCookie(cookie);
            log.info("Login web efetuado para {}", email);
            return "redirect:/sistemas";
        } catch (RuntimeException ex) {
            log.warn("Login web recusado para {}: {}", email, ex.getMessage());
            model.addAttribute("error", "Credenciais inválidas.");
            return "login";
        }
    }

    @GetMapping("/logout")
    public String sair(HttpServletResponse response) {
        Cookie cookie = new Cookie(COOKIE_TOKEN, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Teste> testes = testeRepository.findAll();
        Map<String, Object> stats = new LinkedHashMap<>();
        for (StatusTeste status : StatusTeste.values()) {
            stats.put(chave(status), testes.stream().filter(t -> t.getStatus() == status).count());
        }
        model.addAttribute("stats", stats);
        model.addAttribute("testesRecentes", testes.stream()
                .sorted(Comparator.comparing(Teste::getId,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(WebController::viewTeste)
                .toList());
        return "dashboard";
    }

    @GetMapping("/sistemas")
    public String sistemas(Model model) {
        model.addAttribute("sistemas", sistemaRepository.findByAtivoTrue().stream()
                .map(WebController::viewSistema)
                .toList());
        return "sistemas";
    }

    @GetMapping("/testes/{id}")
    public String detalheTeste(@PathVariable Long id, Model model) {
        Optional<Teste> encontrado = testeRepository.findById(id);
        if (encontrado.isEmpty()) {
            return "redirect:/dashboard";
        }
        Teste teste = encontrado.get();
        List<Bug> bugs = bugRepository.findByTesteId(id);

        Map<String, Long> porTipo = new LinkedHashMap<>();
        for (TipoBug tipo : TipoBug.values()) {
            porTipo.put(tipo.name(), 0L);
        }
        for (Bug bug : bugs) {
            porTipo.merge(bug.getTipo().name(), 1L, Long::sum);
        }
        List<Map<String, Object>> scannerResults = new ArrayList<>();
        for (TipoBug tipo : TipoBug.values()) {
            long total = porTipo.getOrDefault(tipo.name(), 0L);
            Map<String, Object> linha = new LinkedHashMap<>();
            linha.put("nome", nomeScanner(tipo));
            linha.put("issueCount", total);
            linha.put("passed", total == 0);
            scannerResults.add(linha);
        }

        model.addAttribute("teste", viewTeste(teste));
        model.addAttribute("bugs", bugs.stream().map(WebController::viewBug).toList());
        model.addAttribute("scannerResults", scannerResults);
        return "teste-detalhe";
    }

    // ── Views (mapas simples para as telas) ─────────────────────────────

    private static Map<String, Object> viewSistema(Sistema sistema) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", sistema.getId());
        view.put("nome", sistema.getNome());
        view.put("urlBase", sistema.getUrlBase());
        view.put("ambiente", sistema.getAmbiente() == null ? "-" : sistema.getAmbiente().name());
        view.put("ambienteClass", sistema.getAmbiente() == null ? "dev"
                : sistema.getAmbiente().name().toLowerCase());
        view.put("profundidadeCrawl", sistema.getProfundidadeCrawl());
        view.put("modoCrawler", sistema.getModoCrawler() == null ? "-" : sistema.getModoCrawler().name());
        return view;
    }

    private static Map<String, Object> viewTeste(Teste teste) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", teste.getId());
        view.put("sistemaId", teste.getSistema() == null ? null : teste.getSistema().getId());
        view.put("sistemaNome", teste.getSistema() == null ? "-" : teste.getSistema().getNome());
        view.put("urlBase", teste.getSistema() == null ? "-" : teste.getSistema().getUrlBase());
        view.put("status", teste.getStatus() == null ? "-" : teste.getStatus().name());
        view.put("statusClass", teste.getStatus() == null ? "queued"
                : teste.getStatus().name().toLowerCase().replace('_', '-'));
        view.put("iniciadoEm", formato(teste.getInicioEm()));
        view.put("finalizadoEm", formato(teste.getFimEm()));
        view.put("paginasVisitadas", teste.getPaginasVisitadas());
        view.put("totalBugs", teste.getTotalBugs());
        return view;
    }

    private static Map<String, Object> viewBug(Bug bug) {
        Map<String, Object> view = new LinkedHashMap<>();
        view.put("id", bug.getId());
        view.put("severidade", bug.getSeveridade() == null ? "-" : bug.getSeveridade().name());
        view.put("severidadeClass", bug.getSeveridade() == null ? "low"
                : bug.getSeveridade().name().toLowerCase());
        view.put("tipo", bug.getTipo() == null ? "-" : bug.getTipo().name());
        view.put("url", bug.getUrl());
        view.put("mensagem", bug.getMensagem());
        view.put("detalhe", bug.getLinha() == null ? bug.getUrl()
                : bug.getUrl() + " (linha " + bug.getLinha() + ")");
        return view;
    }

    private static String formato(java.time.LocalDateTime quando) {
        return quando == null ? "-" : quando.format(DATA_HORA);
    }

    /** Chave do contador no dashboard para cada status (inclui o do login assistido). */
    private static String chave(StatusTeste status) {
        return switch (status) {
            case QUEUED -> "queued";
            case AGUARDANDO_LOGIN -> "aguardandoLogin";
            case RUNNING -> "running";
            case COMPLETED -> "completed";
            case FAILED -> "failed";
        };
    }

    private static String nomeScanner(TipoBug tipo) {
        return switch (tipo) {
            case HTTP_ERROR -> "HTTP Scanner";
            case JS_ERROR -> "JavaScript Scanner";
            case BROKEN_IMAGE -> "Image Scanner";
        };
    }
}
