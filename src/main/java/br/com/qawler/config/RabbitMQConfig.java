package br.com.qawler.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration — declares queues, exchanges, bindings and message converter.
 */
@Configuration
public class RabbitMQConfig {

    // ── Queue names ──────────────────────────────────────
    public static final String QUEUE_TESTES      = "qawler.testes";
    public static final String QUEUE_NOTIFICACOES = "qawler.notificacoes";
    public static final String QUEUE_CRAWLER      = "qawler.crawler";
    public static final String QUEUE_DEAD_LETTER  = "qawler.dlq";

    // ── Exchange names ──────────────────────────────────
    public static final String EXCHANGE_DIRECT = "qawler.direct";
    public static final String EXCHANGE_DLX    = "qawler.dlx";

    // ── Routing keys ────────────────────────────────────
    public static final String RK_TESTES       = "qawler.testes";
    public static final String RK_NOTIFICACOES  = "qawler.notificacoes";
    public static final String RK_CRAWLER       = "qawler.crawler";

    // ── Queues ──────────────────────────────────────────

    @Bean
    public Queue queueTestes() {
        return QueueBuilder.durable(QUEUE_TESTES)
                .deadLetterExchange(EXCHANGE_DLX)
                .deadLetterRoutingKey(QUEUE_DEAD_LETTER)
                .build();
    }

    @Bean
    public Queue queueNotificacoes() {
        return QueueBuilder.durable(QUEUE_NOTIFICACOES)
                .deadLetterExchange(EXCHANGE_DLX)
                .deadLetterRoutingKey(QUEUE_DEAD_LETTER)
                .build();
    }

    @Bean
    public Queue queueCrawler() {
        return QueueBuilder.durable(QUEUE_CRAWLER)
                .deadLetterExchange(EXCHANGE_DLX)
                .deadLetterRoutingKey(QUEUE_DEAD_LETTER)
                .build();
    }

    @Bean
    public Queue queueDeadLetter() {
        return QueueBuilder.durable(QUEUE_DEAD_LETTER).build();
    }

    // ── Exchanges ───────────────────────────────────────

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(EXCHANGE_DIRECT);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    // ── Bindings ────────────────────────────────────────

    @Bean
    public Binding bindingTestes(Queue queueTestes, DirectExchange directExchange) {
        return BindingBuilder.bind(queueTestes).to(directExchange).with(RK_TESTES);
    }

    @Bean
    public Binding bindingNotificacoes(Queue queueNotificacoes, DirectExchange directExchange) {
        return BindingBuilder.bind(queueNotificacoes).to(directExchange).with(RK_NOTIFICACOES);
    }

    @Bean
    public Binding bindingCrawler(Queue queueCrawler, DirectExchange directExchange) {
        return BindingBuilder.bind(queueCrawler).to(directExchange).with(RK_CRAWLER);
    }

    @Bean
    public Binding bindingDeadLetter(Queue queueDeadLetter, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(queueDeadLetter).to(deadLetterExchange).with(QUEUE_DEAD_LETTER);
    }

    // ── Message Converter ───────────────────────────────

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                          Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }
}
