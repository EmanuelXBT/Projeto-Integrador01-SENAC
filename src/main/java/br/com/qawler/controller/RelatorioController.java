package br.com.qawler.controller;

import br.com.qawler.service.RelatorioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for reports — list, generate PDF, download.
 */
@RestController
@RequestMapping("/api/relatorios")
public class RelatorioController {

    private static final Logger log = LoggerFactory.getLogger(RelatorioController.class);

    private final RelatorioService relatorioService;

    public RelatorioController(RelatorioService relatorioService) {
        this.relatorioService = relatorioService;
    }

    /**
     * GET  /api/relatorios?testeId=   — list reports for a test
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar(@RequestParam(required = false) Long testeId) {
        log.info("GET /api/relatorios (testeId={})", testeId);
        if (testeId != null) {
            return ResponseEntity.ok(relatorioService.listarPorTeste(testeId));
        }
        return ResponseEntity.ok(List.of());
    }

    /**
     * GET  /api/relatorios/{id}       — get report metadata by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        log.info("GET /api/relatorios/{}", id);
        return relatorioService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/relatorios/pdf        — generate and download a PDF report
     */
    @PostMapping("/pdf")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<byte[]> gerarPdf(@RequestBody Map<String, Object> payload) {
        Long testId = payload.get("testeId") instanceof Number n ? n.longValue() : null;
        String sistemaNome = (String) payload.getOrDefault("sistemaNome", "Desconhecido");

        @SuppressWarnings("unchecked")
        List<String> bugs = (List<String>) payload.getOrDefault("bugs", List.of());

        if (testId == null) {
            throw new IllegalArgumentException("testeId é obrigatório");
        }

        log.info("POST /api/relatorios/pdf — testId={}", testId);
        byte[] pdf = relatorioService.gerarPdf(testId, sistemaNome, bugs);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "relatorio-" + testId + ".pdf");

        return ResponseEntity.ok().headers(headers).body(pdf);
    }
}
