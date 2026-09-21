package br.com.qawler.config;

/**
 * Cookie que carrega o JWT na <b>interface web</b> (login pelo navegador).
 *
 * <p>A API REST continua stateless com {@code Authorization: Bearer}; o
 * {@link JwtAuthFilter} aceita as duas formas, então as telas Thymeleaf podem
 * chamar os mesmos endpoints sem JavaScript de token.
 */
public final class AuthCookie {

    /** Nome do cookie do token (HttpOnly + SameSite=Strict). */
    public static final String NOME = "qawler_token";

    private AuthCookie() {
        // utilitário: não instanciar
    }
}
