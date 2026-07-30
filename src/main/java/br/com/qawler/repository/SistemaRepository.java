package br.com.qawler.repository;

import br.com.qawler.entity.Sistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SistemaRepository extends JpaRepository<Sistema, Long> {

    List<Sistema> findByUsuarioId(Long usuarioId);

    List<Sistema> findByUsuarioIdAndAtivoTrue(Long usuarioId);

    List<Sistema> findByAtivoTrue();
}
