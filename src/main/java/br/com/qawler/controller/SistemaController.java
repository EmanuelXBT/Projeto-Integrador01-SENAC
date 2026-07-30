package br.com.qawler.controller;

import br.com.qawler.service.SistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing sistemas (QA target systems).
 */
@RestController
@RequestMapping("/api/sistemas")
public class SistemaController {

    private static final Logger log = LoggerFactory.getLogger(SistemaController.class);

    private final SistemaService sistemaService;

    public SistemaController(SistemaService sistemaService) {
        this.sistemaService = sistemaService;
    }

    /**
     * GET  /api/sistemas          — list all active sistemas
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> listar() {
        log.info("GET /api/sistemas");
        var sistemas = sistemaService.listarTodos();
        return ResponseEntity.ok(sistemas);
    }

    /**
     * GET  /api/sistemas/{id}     — get a single sistema by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> buscar(@PathVariable Long id) {
        log.info("GET /api/sistemas/{}", id);
        return sistemaService.buscarPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/sistemas          — create a new sistema
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> criar(@RequestBody Map<String, Object> payload) {
        log.info("POST /api/sistemas — payload: {}", payload);
        Object saved = sistemaService.salvar(payload);
        return ResponseEntity.ok(saved);
    }

    /**
     * DELETE /api/sistemas/{id}   — soft-delete (deactivate) a sistema
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> desativar(@PathVariable Long id) {
        log.info("DELETE /api/sistemas/{}", id);
        sistemaService.desativar(id);
        return ResponseEntity.noContent().build();
    }
}
