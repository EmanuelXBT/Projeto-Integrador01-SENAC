package br.com.qawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioResponse {

    private Long testeId;
    private String sistemaNome;
    private Integer totalPaginasVisitadas;
    private Integer totalBugs;
    private Integer criticos;
    private Integer altos;
    private Integer medios;
    private Integer baixos;
    private Integer duracaoMs;
}
