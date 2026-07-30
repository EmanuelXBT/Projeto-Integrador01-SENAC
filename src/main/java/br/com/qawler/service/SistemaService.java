package br.com.qawler.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * CRUD service for sistemas (target systems under QA monitoring).
 *
 * Expected entity: {@code br.com.qawler.model.Sistema} with fields:
 *   id (Long), nome (String), urlBase (String), descricao (String),
 *   ambiente (Ambiente), ativo (Boolean), criadoEm (LocalDateTime)
 */
@Service
public class SistemaService {

    private static final Logger log = LoggerFactory.getLogger(SistemaService.class);

    // Replace with real SistemaRepository when entity layer exists
    // private final SistemaRepository sistemaRepository;

    public SistemaService(/* SistemaRepository sistemaRepository */) {
        // this.sistemaRepository = sistemaRepository;
    }

    /**
     * Lists all active sistemas.
     */
    public List<Object> listarTodos() {
        log.info("Listing all sistemas");
        // return sistemaRepository.findAllByAtivoTrue();
        return List.of();  // stub
    }

    /**
     * Finds a sistema by its ID.
     */
    public Optional<Object> buscarPorId(Long id) {
        log.info("Finding sistema by id: {}", id);
        // return sistemaRepository.findById(id);
        return Optional.empty();  // stub
    }

    /**
     * Creates or updates a sistema.
     */
    public Object salvar(Object sistema) {
        log.info("Saving sistema: {}", sistema);
        // return sistemaRepository.save(sistema);
        return sistema;  // stub
    }

    /**
     * Soft-deletes a sistema (sets ativo = false).
     */
    public void desativar(Long id) {
        log.info("Deactivating sistema id: {}", id);
        // sistemaRepository.findById(id).ifPresent(s -> {
        //     s.setAtivo(false);
        //     sistemaRepository.save(s);
        // });
    }
}
