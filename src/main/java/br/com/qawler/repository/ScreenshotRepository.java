package br.com.qawler.repository;

import br.com.qawler.entity.Screenshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScreenshotRepository extends JpaRepository<Screenshot, Long> {

    List<Screenshot> findByBugId(Long bugId);

    List<Screenshot> findByBugIdOrderByCriadoEmDesc(Long bugId);

    List<Screenshot> findByPaginaVisitadaId(Long paginaVisitadaId);

    void deleteByBugId(Long bugId);
}
