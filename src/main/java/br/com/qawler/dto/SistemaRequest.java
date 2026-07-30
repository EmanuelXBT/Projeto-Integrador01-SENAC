package br.com.qawler.dto;

import br.com.qawler.enums.Ambiente;
import br.com.qawler.enums.ModoCrawler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SistemaRequest {

    private String nome;
    private String urlBase;
    private Ambiente ambiente;
    private String credenciaisLogin;
    private String credenciaisSenha;
    private String dominiosAutorizados;
    private Integer profundidadeCrawl;
    private ModoCrawler modoCrawler;
    private Boolean autorizadoProducao;
}
