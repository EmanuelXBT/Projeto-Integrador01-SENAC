package br.com.qawler.repository;

import br.com.qawler.entity.Bug;
import br.com.qawler.enums.Severidade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    List<Bug> findByTesteId(Long testeId);

    List<Bug> findByTesteIdOrderByDetectadoEmDesc(Long testeId);

    List<Bug> findByPaginaVisitadaId(Long paginaVisitadaId);

    List<Bug> findByTesteIdAndSeveridade(Long testeId, Severidade severidade);

    long countByTesteId(Long testeId);

    long countByTesteIdAndSeveridade(Long testeId, Severidade severidade);
}
