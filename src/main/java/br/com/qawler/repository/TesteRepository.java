package br.com.qawler.repository;

import br.com.qawler.entity.Teste;
import br.com.qawler.enums.StatusTeste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TesteRepository extends JpaRepository<Teste, Long> {

    List<Teste> findBySistemaIdOrderByCriadoEmDesc(Long sistemaId);

    List<Teste> findBySistemaIdAndStatusOrderByCriadoEmDesc(Long sistemaId, StatusTeste status);

    List<Teste> findBySistema_Usuario_IdOrderByCriadoEmDesc(Long usuarioId);

    List<Teste> findByStatus(StatusTeste status);

    List<Teste> findByAgendamentoId(Long agendamentoId);

    Optional<Teste> findTopBySistemaIdOrderByCriadoEmDesc(Long sistemaId);

    long countBySistemaId(Long sistemaId);
}
