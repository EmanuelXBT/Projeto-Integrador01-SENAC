package br.com.qawler.dto;

import br.com.qawler.enums.StatusTeste;
import br.com.qawler.enums.TipoDisparo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TesteResponse {

    private Long id;
    private Long sistemaId;
    private String sistemaNome;
    private StatusTeste status;
    private TipoDisparo tipoDisparo;
    private Long agendamentoId;
    private Integer paginasVisitadas;
    private Integer totalBugs;
    private Integer duracaoMs;
    private String erroMensagem;
    private LocalDateTime inicioEm;
    private LocalDateTime fimEm;
    private LocalDateTime criadoEm;
}
