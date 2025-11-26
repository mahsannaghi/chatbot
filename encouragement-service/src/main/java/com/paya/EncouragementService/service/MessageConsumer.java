package com.paya.EncouragementService.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
public class MessageConsumer {

    @RabbitListener(queues = "initial_queue")
    public void receiveMessage(String message) {
        System.out.println("Received message: " + message);
        // پردازش پیام دریافتی
    }
}
