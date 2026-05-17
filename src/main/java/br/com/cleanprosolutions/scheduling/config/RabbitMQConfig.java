package br.com.cleanprosolutions.scheduling.config;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ configuration for the scheduling service.
 *
 * <p>Configures the exchange to publish {@code ScheduleCreated} events
 * and the JSON message converter.</p>
 *
 * @author Emerson Lima
 * @since 1.0.0
 */
@Configuration
public class RabbitMQConfig {

    @Value("${rabbitmq.exchange.scheduling:scheduling.exchange}")
    private String schedulingExchange;

    @Bean
    public TopicExchange schedulingExchange() {
        return new TopicExchange(schedulingExchange, true, false);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
