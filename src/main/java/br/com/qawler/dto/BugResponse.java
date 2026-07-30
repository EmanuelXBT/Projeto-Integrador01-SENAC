package br.com.qawler.dto;

import br.com.qawler.enums.Severidade;
import br.com.qawler.enums.TipoBug;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugResponse {

    private Long id;
    private Long testeId;
    private Long paginaVisitadaId;
    private String paginaVisitadaUrl;
    private TipoBug tipo;
    private Severidade severidade;
    private String url;
    private String mensagem;
    private Integer linha;
    private Integer coluna;
    private LocalDateTime detectadoEm;
}
