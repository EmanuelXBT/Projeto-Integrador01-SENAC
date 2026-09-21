package br.com.qawler.controller;

import br.com.qawler.dto.SessaoResponse;
import br.com.qawler.service.SessaoAssistidaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.function.Supplier;

/**
 * Login assistido — sessão autenticada do usuário (docs/LOGIN-ASSISTIDO.md).
 *
 * <p>Fluxo: {@code /abrir} (janela na máquina do usuário ou registro da estação) →
 * login manual → {@code /confirmar} → o crawler usa a sessão (E3).</p>
 *
 * <ul>
 *   <li>{@code GET  /api/sistemas/{id}/sessao} — estado atual (404 se não há sessão)</li>
 *   <li>{@code POST /api/sistemas/{id}/sessao/abrir?estacao=host:porta} — abre (M1) ou registra a estação (M2)</li>
 *   <li>{@code POST /api/sistemas/{id}/sessao/anexar?estacao=host:porta} — adota janela já aberta</li>
 *   <li>{@code POST /api/sistemas/{id}/sessao/testar?estacao=host:porta} — testa a conexão com a estação</li>
 *   <li>{@code POST /api/sistemas/{id}/sessao/confirmar} — “Concluí o login”</li>
 *   <li>{@code POST /api/sistemas/{id}/sessao/encerrar} — remove a sessão do registro</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/sistemas/{id}/sessao")
@PreAuthorize("isAuthenticated()")
public class SessaoController {

    private static final Logger log = LoggerFactory.getLogger(SessaoController.class);

    private final SessaoAssistidaService sessaoService;

    public SessaoController(SessaoAssistidaService sessaoService) {
        this.sessaoService = sessaoService;
    }

    @GetMapping
    public ResponseEntity<?> consultar(@PathVariable Long id) {
        log.info("GET /api/sistemas/{}/sessao", id);
        return sessaoService.consultar(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/abrir")
    public ResponseEntity<?> abrir(@PathVariable Long id,
                                   @RequestParam(required = false) String estacao) {
        log.info("POST /api/sistemas/{}/sessao/abrir — estacao={}", id, estacao);
        return responder(() -> sessaoService.abrir(id, estacao));
    }

    @PostMapping("/anexar")
    public ResponseEntity<?> anexar(@PathVariable Long id,
                                    @RequestParam(required = false) String estacao) {
        log.info("POST /api/sistemas/{}/sessao/anexar — estacao={}", id, estacao);
        return responder(() -> sessaoService.anexar(id, estacao));
    }

    @PostMapping("/testar")
    public ResponseEntity<?> testar(@PathVariable Long id,
                                    @RequestParam(required = false) String estacao) {
        log.info("POST /api/sistemas/{}/sessao/testar — estacao={}", id, estacao);
        return ResponseEntity.ok(sessaoService.testar(estacao));
    }

    @PostMapping("/confirmar")
    public ResponseEntity<?> confirmar(@PathVariable Long id) {
        log.info("POST /api/sistemas/{}/sessao/confirmar", id);
        return responder(() -> sessaoService.confirmar(id));
    }

    @PostMapping("/encerrar")
    public ResponseEntity<?> encerrar(@PathVariable Long id) {
        log.info("POST /api/sistemas/{}/sessao/encerrar", id);
        sessaoService.encerrar(id);
        return ResponseEntity.noContent().build();
    }

    /** Converte falha de regra em resposta legível (400/409) em vez de stacktrace. */
    private ResponseEntity<?> responder(Supplier<SessaoResponse> acao) {
        try {
            return ResponseEntity.ok(acao.get());
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("erro", String.valueOf(ex.getMessage())));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(409).body(Map.of("erro", String.valueOf(ex.getMessage())));
        }
    }
}
