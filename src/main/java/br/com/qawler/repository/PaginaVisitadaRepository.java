package br.com.qawler.repository;

import br.com.qawler.entity.PaginaVisitada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaginaVisitadaRepository extends JpaRepository<PaginaVisitada, Long> {

    List<PaginaVisitada> findByTesteId(Long testeId);

    List<PaginaVisitada> findByTesteIdOrderByVisitadaEmAsc(Long testeId);

    Optional<PaginaVisitada> findByTesteIdAndUrl(Long testeId, String url);

    long countByTesteId(Long testeId);
}
