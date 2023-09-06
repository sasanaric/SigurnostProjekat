package com.example.sigurnostprojekat.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.MultiRabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.annotation.RabbitListenerAnnotationBeanPostProcessor;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactoryContextWrapper;
import org.springframework.amqp.rabbit.connection.SimpleRoutingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitConfig {

    @Value("${rabbitmq.queue1.name}")
    private String queueName1;
    @Value("${rabbitmq.queue2.name}")
    private String queueName2;
    @Value("${rabbitmq.queue3.name}")
    private String queueName3;
    @Value("${rabbitmq.queue4.name}")
    private String queueName4;

    @Bean
    public Queue queue1() {
        return new Queue(queueName1, true);
    }
    @Bean
    public Queue queue2() {
        return new Queue(queueName2, true);
    }
    @Bean
    public Queue queue3() {
        return new Queue(queueName3, true);
    }
    @Bean
    public Queue queue4() {
        return new Queue(queueName4, true);
    }
    @Bean(name = "connectionFactory1")
    public ConnectionFactory connectionFactory1(
            @Value("${spring.rabbitmq.server1.host}") String host,
            @Value("${spring.rabbitmq.server1.port}") int port,
            @Value("${spring.rabbitmq.server1.username}") String username,
            @Value("${spring.rabbitmq.server1.password}") String password) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean(name = "rabbitTemplate1")
    public RabbitTemplate rabbitTemplate1(@Qualifier("connectionFactory1") ConnectionFactory connectionFactory1) {
        return new RabbitTemplate(connectionFactory1);
    }

    @Bean(name = "connectionFactory2")
    public ConnectionFactory connectionFactory2(
            @Value("${spring.rabbitmq.server2.host}") String host,
            @Value("${spring.rabbitmq.server2.port}") int port,
            @Value("${spring.rabbitmq.server2.username}") String username,
            @Value("${spring.rabbitmq.server2.password}") String password) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean(name = "rabbitTemplate2")
    public RabbitTemplate rabbitTemplate2(@Qualifier("connectionFactory2") ConnectionFactory connectionFactory2) {
        return new RabbitTemplate(connectionFactory2);
    }

    @Bean(name = "connectionFactory3")
    public ConnectionFactory connectionFactory3(
            @Value("${spring.rabbitmq.server3.host}") String host,
            @Value("${spring.rabbitmq.server3.port}") int port,
            @Value("${spring.rabbitmq.server3.username}") String username,
            @Value("${spring.rabbitmq.server3.password}") String password) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean(name = "rabbitTemplate3")
    public RabbitTemplate rabbitTemplate3(@Qualifier("connectionFactory3") ConnectionFactory connectionFactory3) {
        return new RabbitTemplate(connectionFactory3);
    }

    @Bean(name = "connectionFactory4")
    public ConnectionFactory connectionFactory4(
            @Value("${spring.rabbitmq.server4.host}") String host,
            @Value("${spring.rabbitmq.server4.port}") int port,
            @Value("${spring.rabbitmq.server4.username}") String username,
            @Value("${spring.rabbitmq.server4.password}") String password) {
        CachingConnectionFactory factory = new CachingConnectionFactory(host);
        factory.setPort(port);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory;
    }

    @Bean(name = "rabbitTemplate4")
    public RabbitTemplate rabbitTemplate4(@Qualifier("connectionFactory4") ConnectionFactory connectionFactory4) {
        return new RabbitTemplate(connectionFactory4);
    }

    @Bean
    SimpleRoutingConnectionFactory rcf(
            @Qualifier("connectionFactory1") ConnectionFactory cf1,
            @Qualifier("connectionFactory2") ConnectionFactory cf2,
            @Qualifier("connectionFactory3") ConnectionFactory cf3,
            @Qualifier("connectionFactory4") ConnectionFactory cf4) {

        SimpleRoutingConnectionFactory rcf = new SimpleRoutingConnectionFactory();
        rcf.setDefaultTargetConnectionFactory(cf1);
        rcf.setTargetConnectionFactories(Map.of("one", cf1, "two", cf2, "three", cf3,"four",cf4));
        return rcf;
    }

    @Bean("factory1-admin")
    RabbitAdmin admin1(@Qualifier("connectionFactory1") ConnectionFactory cf1) {
        return new RabbitAdmin(cf1);
    }

    @Bean("factory2-admin")
    RabbitAdmin admin2(@Qualifier("connectionFactory2") ConnectionFactory cf2) {
        return new RabbitAdmin(cf2);
    }

    @Bean("factory3-admin")
    RabbitAdmin admin3(@Qualifier("connectionFactory3") ConnectionFactory cf3) {
        return new RabbitAdmin(cf3);
    }

    @Bean("factory4-admin")
    RabbitAdmin admin4(@Qualifier("connectionFactory4") ConnectionFactory cf4) {
        return new RabbitAdmin(cf4);
    }


    @Bean
    public RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry() {
        return new RabbitListenerEndpointRegistry();
    }

    @Bean
    public RabbitListenerAnnotationBeanPostProcessor postProcessor(RabbitListenerEndpointRegistry registry) {
        MultiRabbitListenerAnnotationBeanPostProcessor postProcessor
                = new MultiRabbitListenerAnnotationBeanPostProcessor();
        postProcessor.setEndpointRegistry(registry);
        postProcessor.setContainerFactoryBeanName("defaultContainerFactory");
        return postProcessor;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory1(@Qualifier("connectionFactory1") ConnectionFactory cf1) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf1);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory2(@Qualifier("connectionFactory2") ConnectionFactory cf2) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf2);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory3(@Qualifier("connectionFactory3") ConnectionFactory cf3) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf3);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory factory4(@Qualifier("connectionFactory4") ConnectionFactory cf4) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf4);
        return factory;
    }

    @Bean
    RabbitTemplate template(SimpleRoutingConnectionFactory rcf) {
        return new RabbitTemplate(rcf);
    }

    @Bean
    ConnectionFactoryContextWrapper wrapper(SimpleRoutingConnectionFactory rcf) {
        return new ConnectionFactoryContextWrapper(rcf);
    }
}