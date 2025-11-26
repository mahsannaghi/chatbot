package com.paya.EncouragementService.config;

import com.paya.EncouragementService.entity.TblQueue;
import com.paya.EncouragementService.repository.QueueRepository;
import com.paya.EncouragementService.service.RabbitMQListenerAndProducer;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.util.*;

@EnableRabbit
@Configuration
public class RabbitMQConfig {

    public static final String INIT_REQUEST_KEY = "INIT_REQUEST_KEY30";
    public static final String INIT_RESPONSE_KEY = "INIT_RESPONSE_KEY30";
    private final QueueRepository queueRepository;


    @Autowired
    @Lazy
    private RabbitMQListenerAndProducer listener;
    private SimpleMessageListenerContainer container;
    private final Set<String> ALL_QUEUES;

    @Value("${encouragement.requestQueueSuffix}")
    private String requestQueueSuffix;

    @Value("${encouragement.responseQueueSuffix}")
    private String responseQueueSuffix;

    @Value("${encouragement.consumerName}")
    private String consumerName;

    public RabbitMQConfig(QueueRepository queueRepository) {
        this.queueRepository = queueRepository;
        ALL_QUEUES = new HashSet<>();
        addConsumerResponseQueueName();
    }

    private void addConsumerResponseQueueName() {
        List<TblQueue> queues = queueRepository.findAll();
        if (queues.size() != 0) {
            TblQueue queue = queues.get(0);
            if (queue != null) {
                if (queue.getResponseQueueName() != null)
                    ALL_QUEUES.add(queue.getResponseQueueName());
            }
        } else
            ALL_QUEUES.add(RabbitMQConfig.INIT_RESPONSE_KEY);
    }

    @Bean
    public CachingConnectionFactory getCachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory("10.16.113.23");
        cachingConnectionFactory.setHost("10.16.113.23");
        cachingConnectionFactory.setPort(5672);
        cachingConnectionFactory.setUsername("admin");
        cachingConnectionFactory.setPassword("admin");
        cachingConnectionFactory.setVirtualHost("/");
        cachingConnectionFactory.setChannelCacheSize(1);
        cachingConnectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        return cachingConnectionFactory;
    }

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setMessageListener(messageListenerAdapter(listener));
        container.setQueueNames(ALL_QUEUES.toArray(new String[0]));
        container.setAutoDeclare(true);
        container.setMessageListener((message) -> {
            String queueName = message.getMessageProperties().getConsumerQueue();
            byte[] body = message.getBody();
            listener.receiveMessage(body, queueName);
        });
        return container;
    }


    @Bean
    public MessageListenerAdapter messageListenerAdapter(RabbitMQListenerAndProducer listener) {
        return new MessageListenerAdapter(listener, "receiveMessage");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }


    public void createQueue(String requestQueueName, String responseQueueName) {
        if (queueRepository.findAll().size() != 0) {
            queueRepository.deleteAll();
        }
        TblQueue queue = TblQueue.builder()
                .requestQueueName(requestQueueName)
                .responseQueueName(responseQueueName)
                .build();
        queueRepository.save(queue);
    }

    public void addQueue(String requestQueueName, String responseQueueName, RabbitAdmin rabbitAdmin) {
        try {
            if (responseQueueName.toUpperCase().contains(consumerName.toUpperCase())) {
                if (container == null) {
                    container = messageListenerContainer(getCachingConnectionFactory());
                }
                String[] queueNames = container.getQueueNames();
                boolean existBefore = Arrays.asList(queueNames).contains(responseQueueName);
                if (!existBefore) {
                    org.springframework.amqp.core.Queue newQueue = new Queue(responseQueueName, true);
                    rabbitAdmin.declareQueue(newQueue);
                    container.addQueueNames(responseQueueName);
                    ALL_QUEUES.add(responseQueueName);
                    container.addQueueNames(responseQueueName);
                    String[] queueNamesAfterAdding = container.getQueueNames();
                    boolean existAfter = Arrays.asList(queueNamesAfterAdding).contains(responseQueueName);
                    if (!existAfter) {
                        if (container.isRunning()) {
                            container.stop();
                        }
                        container.start();
                    } else {
                        container.removeQueueNames(INIT_RESPONSE_KEY);
                        ALL_QUEUES.remove(INIT_RESPONSE_KEY);
                    }
                }
                if (queueRepository.findAll().isEmpty())
                    createQueue(requestQueueName, responseQueueName);
                else {
                    TblQueue queue = queueRepository.findAll().get(0);
                    if (!queue.getResponseQueueName().equals(responseQueueName))
                        createQueue(requestQueueName, responseQueueName);
                }
            }
        } catch (Exception e) {
            System.out.println("An error has occurred." + e);
        }
    }

    public void removeQueue(String queueName, RabbitTemplate rabbitTemplate) {
        try {
            if (!Objects.equals(queueName, INIT_RESPONSE_KEY)) {
                if (container == null) {
//                    container = messageListenerContainer(rabbitTemplate);
                    System.out.println("Container is not initialized.");
                }
                ALL_QUEUES.remove(queueName);
                queueRepository.deleteAll();
                String[] queueNames = container.getQueueNames();
                boolean exists = Arrays.asList(queueNames).contains(queueName);

                if (exists) {
                    container.removeQueueNames(queueName);
                } else {
                    System.out.println("Queue " + queueName + " does not exist in the container.");
                }
            }
        } catch (Exception e) {
            System.out.println("An error has occurred." + e);
        }
    }
}

