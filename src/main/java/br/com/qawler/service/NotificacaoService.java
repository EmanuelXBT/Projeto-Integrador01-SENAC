package br.com.qawler.service;

import br.com.qawler.enums.TipoNotificacao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Sends notifications via email and/or RabbitMQ message queues.
 *
 * Expected entity: {@code br.com.qawler.model.Notificacao} with fields:
 *   id (Long), destinatario (String), tipo (TipoNotificacao), mensagem (String), enviadaEm (LocalDateTime)
 */
@Service
public class NotificacaoService {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoService.class);

    private final JavaMailSender mailSender;
    private final RabbitTemplate rabbitTemplate;

    // Replace with real repository when entity layer exists
    // private final NotificacaoRepository notificacaoRepository;

    public NotificacaoService(JavaMailSender mailSender,
                              RabbitTemplate rabbitTemplate
                              /* , NotificacaoRepository notificacaoRepository */) {
        this.mailSender = mailSender;
        this.rabbitTemplate = rabbitTemplate;
        // this.notificacaoRepository = notificacaoRepository;
    }

    /**
     * Sends an email notification to a list of recipients.
     */
    public void enviarEmail(List<String> destinatarios, String assunto, String corpo) {
        log.info("Sending email to {} recipients — subject: {}", destinatarios.size(), assunto);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(destinatarios.toArray(new String[0]));
            message.setSubject(assunto);
            message.setText(corpo);
            mailSender.send(message);
            log.info("Email sent successfully");
        } catch (Exception e) {
            log.error("Failed to send email: {}", e.getMessage(), e);
        }
    }

    /**
     * Publishes a notification to the RabbitMQ queue for async processing.
     */
    public void publicarNaFila(String routingKey, Object payload) {
        log.info("Publishing message to routing key '{}'", routingKey);
        try {
            rabbitTemplate.convertAndSend(routingKey, payload);
            log.info("Message published successfully");
        } catch (Exception e) {
            log.error("Failed to publish message: {}", e.getMessage(), e);
        }
    }

    /**
     * Notifies about a test result — sends both email and RabbitMQ messages.
     */
    public void notificarResultadoTeste(Long testeId, TipoNotificacao tipo, String detalhes,
                                         List<String> destinatarios) {
        String assunto = tipo == TipoNotificacao.TESTE_COM_BUGS
                ? "[QAwler] Bugs encontrados — Teste #" + testeId
                : "[QAwler] Falha na execução — Teste #" + testeId;

        String corpo = """
                QAwler — Notificação Automática
                ─────────────────────────────────

                Teste: #%d
                Tipo:  %s
                Detalhes:
                %s
                """.formatted(testeId, tipo, detalhes);

        enviarEmail(destinatarios, assunto, corpo);
        publicarNaFila("qawler.notificacoes", new NotificacaoPayload(testeId, tipo.name(), detalhes));
    }

    // ── Inner payload DTO ────────────────────────────────

    public record NotificacaoPayload(Long testeId, String tipo, String detalhes) {}
}
