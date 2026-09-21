package br.com.qawler.dto;

import java.time.Instant;

/**
 * Estado da sessão assistida de um sistema (login assistido — docs/LOGIN-ASSISTIDO.md).
 *
 * @param sistemaId       sistema avaliado
 * @param aberta          existe sessão registrada na aplicação
 * @param conectada       o endpoint CDP respondeu ({@code /json/version})
 * @param aguardandoLogin o usuário ainda não confirmou o login
 * @param modo            {@code LOCAL} (M1: janela na própria máquina) ou {@code REMOTO} (M2: estação)
 * @param estacao         host:porta informado para a estação (vazio = máquina local)
 * @param enderecoCdp     endereço efetivo usado no {@code debuggerAddress}
 * @param urlAlvo         URL aberta na janela
 * @param urlAtual        página atual da janela (via {@code /json/list})
 * @param navegador       identificação do navegador (ex.: {@code Chrome/140.0.0.0})
 * @param abertaEm        quando a sessão foi registrada
 * @param observacao      avisos legíveis (ex.: binário não encontrado, perfil dedicado)
 */
public record SessaoResponse(
        Long sistemaId,
        boolean aberta,
        boolean conectada,
        boolean aguardandoLogin,
        String modo,
        String estacao,
        String enderecoCdp,
        String urlAlvo,
        String urlAtual,
        String navegador,
        Instant abertaEm,
        String observacao
) {
}
