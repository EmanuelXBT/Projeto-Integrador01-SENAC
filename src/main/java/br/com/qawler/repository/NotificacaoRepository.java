package br.com.qawler.repository;

import br.com.qawler.entity.Notificacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    List<Notificacao> findByUsuarioIdOrderByCriadoEmDesc(Long usuarioId);

    List<Notificacao> findByUsuarioIdAndEnviadoFalse(Long usuarioId);

    List<Notificacao> findByUsuarioIdAndEnviadoFalseOrderByCriadoEmDesc(Long usuarioId);

    long countByUsuarioIdAndEnviadoFalse(Long usuarioId);
}
