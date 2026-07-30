package br.com.qawler.controller;

import br.com.qawler.service.TesteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing test executions.
 */
@RestController
@RequestMapping("/api/testes")
public class TesteController {

    private static final Logger log = LoggerFactory.getLogger(TesteController.class);

    private final TesteService testeService;

    public TesteController(TesteService testeService) {
        this.testeService = testeService;
    }

    /**
     * GET  /api/testes             — list tests, optionally filtered by sistemaId
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar(@RequestParam(required = false) Long sistemaId) {
        log.info("GET /api/testes (sistemaId={})", sistemaId);
        return ResponseEntity.ok(testeService.listar(sistemaId));
    }

    /**
     * GET  /api/testes/{id}        — get a single test by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        log.info("GET /api/testes/{}", id);
        return testeService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/testes             — queue a new test
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<?> agendar(@RequestBody Map<String, Object> payload) {
        Long sistemaId = payload.get("sistemaId") instanceof Number n ? n.longValue() : null;
        String disparo  = (String) payload.getOrDefault("tipoDisparo", "MANUAL");

        if (sistemaId == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "sistemaId é obrigatório"));
        }

        log.info("POST /api/testes — sistemaId={}, tipoDisparo={}", sistemaId, disparo);
        Object teste = testeService.agendar(sistemaId, disparo);
        return ResponseEntity.ok(teste);
    }

    /**
     * GET  /api/testes/stats       — return test count by status
     */
    @GetMapping("/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> stats() {
        log.info("GET /api/testes/stats");
        long queued    = testeService.contarPorStatus(br.com.qawler.enums.StatusTeste.QUEUED);
        long running   = testeService.contarPorStatus(br.com.qawler.enums.StatusTeste.RUNNING);
        long completed = testeService.contarPorStatus(br.com.qawler.enums.StatusTeste.COMPLETED);
        long failed    = testeService.contarPorStatus(br.com.qawler.enums.StatusTeste.FAILED);

        return ResponseEntity.ok(Map.of(
                "queued", queued,
                "running", running,
                "completed", completed,
                "failed", failed
        ));
    }
}
