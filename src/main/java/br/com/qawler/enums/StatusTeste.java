package br.com.qawler.enums;

/**
 * Ciclo de vida de um teste (varredura).
 *
 * <p>{@code AGUARDANDO_LOGIN} é o estado de <b>intervenção humana</b> do login assistido
 * (docs/LOGIN-ASSISTIDO.md): a janela do navegador foi aberta na estação de autenticação
 * e o sistema espera a pessoa concluir o login para então anexar a sessão e varrer.</p>
 */
public enum StatusTeste {
    QUEUED,
    AGUARDANDO_LOGIN,
    RUNNING,
    COMPLETED,
    FAILED
}
