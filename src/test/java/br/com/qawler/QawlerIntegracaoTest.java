package br.com.qawler;

import br.com.qawler.config.AuthCookie;
import br.com.qawler.entity.Bug;
import br.com.qawler.entity.Sistema;
import br.com.qawler.entity.Teste;
import br.com.qawler.entity.Usuario;
import br.com.qawler.enums.Ambiente;
import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.StatusTeste;
import br.com.qawler.enums.TipoBug;
import br.com.qawler.enums.TipoDisparo;
import br.com.qawler.repository.BugRepository;
import br.com.qawler.repository.SistemaRepository;
import br.com.qawler.repository.TesteRepository;
import br.com.qawler.repository.UsuarioRepository;
import br.com.qawler.service.SessaoAssistidaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Testes de integração do QAwler (H2 em memória + MockMvc).
 *
 * <p>Cobrem o login web por cookie, a API com Bearer, o fluxo do <b>login assistido</b>
 * (docs/LOGIN-ASSISTIDO.md) e a renderização das telas de sistemas e de detalhe do teste.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QawlerIntegracaoTest {

    private static final String EMAIL = "admin@qawler.com";
    private static final String SENHA = "admin";

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private SistemaRepository sistemaRepository;
    @Autowired private TesteRepository testeRepository;
    @Autowired private BugRepository bugRepository;
    @Autowired private SessaoAssistidaService sessaoAssistidaService;

    private Long sistemaId;
    private Long testeId;

    @BeforeEach
    void prepararDados() {
        bugRepository.deleteAll();
        testeRepository.deleteAll();
        sistemaRepository.deleteAll();
        usuarioRepository.deleteAll();

        Usuario usuario = new Usuario();
        usuario.setNome("Admin de teste");
        usuario.setEmail(EMAIL);
        usuario.setSenhaHash(passwordEncoder.encode(SENHA));
        usuario.setAtivo(true);
        usuarioRepository.save(usuario);

        Sistema sistema = new Sistema();
        sistema.setUsuario(usuario);
        sistema.setNome("Alvo autenticado");
        sistema.setUrlBase("https://the-internet.herokuapp.com");
        sistema.setAmbiente(Ambiente.STAGING);
        sistema.setDominiosAutorizados("[\"the-internet.herokuapp.com\"]");
        sistema.setAtivo(true);
        sistemaRepository.save(sistema);
        sistemaId = sistema.getId();

        Teste teste = new Teste();
        teste.setSistema(sistema);
        teste.setStatus(StatusTeste.AGUARDANDO_LOGIN);
        teste.setTipoDisparo(TipoDisparo.MANUAL);
        testeRepository.save(teste);
        testeId = teste.getId();

        Bug bug = new Bug();
        bug.setTeste(teste);
        bug.setTipo(TipoBug.BROKEN_IMAGE);
        bug.setSeveridade(Severidade.HIGH);
        bug.setUrl("https://the-internet.herokuapp.com/asdf.jpg");
        bug.setMensagem("Imagem não encontrada (404)");
        bug.setDetectadoEm(LocalDateTime.now());
        bugRepository.save(bug);
    }

    @Test
    @DisplayName("o contexto sobe e o serviço de sessão assistida existe")
    void contextoCarrega() {
        assertThat(sessaoAssistidaService).isNotNull();
    }

    @Test
    @DisplayName("a tela de login é pública")
    void telaDeLoginPublica() throws Exception {
        mvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("QAwler")));
    }

    @Test
    @DisplayName("a API exige token e aceita Bearer")
    void apiExigeToken() throws Exception {
        mvc.perform(get("/api/sistemas")).andExpect(status().is4xxClientError());
        mvc.perform(get("/api/sistemas").header("Authorization", "Bearer " + autenticarNaApi()))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("login web grava cookie HttpOnly e a tela de sistemas traz o painel da sessão")
    void loginWebComCookie() throws Exception {
        MvcResult resultado = mvc.perform(post("/login")
                        .param("email", EMAIL)
                        .param("password", SENHA))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sistemas"))
                .andReturn();

        Cookie cookie = resultado.getResponse().getCookie(AuthCookie.NOME);
        assertThat(cookie).isNotNull();
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getValue()).isNotBlank();

        mvc.perform(get("/sistemas").cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("sessao-assistida")))
                .andExpect(content().string(containsString("Alvo autenticado")));
    }

    @Test
    @DisplayName("login web recusa credenciais inválidas sem revelar detalhes")
    void loginWebInvalido() throws Exception {
        mvc.perform(post("/login").param("email", EMAIL).param("password", "errada"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Credenciais inválidas")));
    }

    @Test
    @DisplayName("o detalhe do teste mostra o estado AGUARDANDO_LOGIN e o painel de login assistido")
    void detalheDoTeste() throws Exception {
        mvc.perform(get("/testes/" + testeId).cookie(new Cookie(AuthCookie.NOME, autenticarNaApi())))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("AGUARDANDO_LOGIN")))
                .andExpect(content().string(containsString("Sessão de login")))
                .andExpect(content().string(containsString("Imagem não encontrada (404)")));
    }

    @Test
    @DisplayName("fluxo completo da sessão assistida no modo remoto (M2)")
    void fluxoSessaoRemota() throws Exception {
        String bearer = "Bearer " + autenticarNaApi();

        mvc.perform(get("/api/sistemas/" + sistemaId + "/sessao").header("Authorization", bearer))
                .andExpect(status().isNotFound());

        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/testar")
                        .param("estacao", "host:abc").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ok").value(false));

        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/abrir")
                        .param("estacao", "100.64.0.10:9222").header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modo").value("REMOTO"))
                .andExpect(jsonPath("$.estacao").value("100.64.0.10:9222"))
                .andExpect(jsonPath("$.aguardandoLogin").value(true))
                .andExpect(jsonPath("$.conectada").value(false));

        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/confirmar")
                        .header("Authorization", bearer))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.aguardandoLogin").value(false));

        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/encerrar")
                        .header("Authorization", bearer))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/sistemas/" + sistemaId + "/sessao").header("Authorization", bearer))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("abrir sessão local sem navegador falha com mensagem clara (409)")
    void sessaoLocalSemNavegador() throws Exception {
        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/abrir")
                        .header("Authorization", "Bearer " + autenticarNaApi()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value(containsString("navegador")));
    }

    @Test
    @DisplayName("confirmar sem sessão registrada devolve 409 explicado")
    void confirmarSemSessao() throws Exception {
        mvc.perform(post("/api/sistemas/" + sistemaId + "/sessao/confirmar")
                        .header("Authorization", "Bearer " + autenticarNaApi()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.erro").value(containsString("nenhuma sessão")));
    }

    @Test
    @DisplayName("sistema inexistente devolve 400 explicado")
    void sistemaInexistente() throws Exception {
        mvc.perform(post("/api/sistemas/999999/sessao/abrir")
                        .param("estacao", "100.64.0.10:9222")
                        .header("Authorization", "Bearer " + autenticarNaApi()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value(containsString("não encontrado")));
    }

    private String autenticarNaApi() throws Exception {
        String corpo = objectMapper.writeValueAsString(Map.of("email", EMAIL, "password", SENHA));
        String resposta = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpo))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(resposta).get("token").asText();
    }
}
