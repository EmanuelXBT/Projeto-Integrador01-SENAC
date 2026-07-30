package br.com.qawler.repository;

import br.com.qawler.entity.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {

    List<Agendamento> findBySistemaId(Long sistemaId);

    List<Agendamento> findBySistema_Usuario_Id(Long usuarioId);

    List<Agendamento> findByAtivoTrue();

    List<Agendamento> findBySistemaIdAndAtivoTrue(Long sistemaId);

    List<Agendamento> findByProximaExecucaoBeforeAndAtivoTrue(LocalDateTime dataHora);

    List<Agendamento> findBySistemaIdAndSistema_Usuario_Id(Long sistemaId, Long usuarioId);
}
