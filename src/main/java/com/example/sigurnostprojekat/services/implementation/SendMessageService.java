package com.example.sigurnostprojekat.services.implementation;

import com.example.sigurnostprojekat.crypto.Crypto;
import com.example.sigurnostprojekat.crypto.Steganography;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.MessagePartition;
import com.example.sigurnostprojekat.models.request.MessageRequest;
import com.example.sigurnostprojekat.services.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SendMessageService {
    private final Steganography steganography;
    private final UserService userService;
    @Value("${rabbitmq.queue1.name}")
    private String queueName1;
    @Value("${rabbitmq.queue2.name}")
    private String queueName2;
    @Value("${rabbitmq.queue3.name}")
    private String queueName3;
    @Value("${rabbitmq.queue4.name}")
    private String queueName4;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    @Qualifier("rabbitTemplate1")
    private RabbitTemplate rabbitTemplate1;

    @Autowired
    @Qualifier("rabbitTemplate2")
    private RabbitTemplate rabbitTemplate2;

    @Autowired
    @Qualifier("rabbitTemplate3")
    private RabbitTemplate rabbitTemplate3;

    @Autowired
    @Qualifier("rabbitTemplate4")
    private RabbitTemplate rabbitTemplate4;

    public SendMessageService(UserService userService) {
        this.userService = userService;
        this.steganography = new Steganography();
    }
    public void sendMessageToServer1(String message) {
        rabbitTemplate1.convertAndSend(queueName1, message);
        System.out.println("sendMessageToServer1-"+message);
    }

    public void sendMessageToServer2(String message) {
        rabbitTemplate2.convertAndSend(queueName2, message);
        System.out.println("sendMessageToServer2-"+message);
    }

    public void sendMessageToServer3(String message) {
        rabbitTemplate3.convertAndSend(queueName3, message);
        System.out.println("sendMessageToServer3-"+message);
    }

    public void sendMessageToServer4(String message) {
        rabbitTemplate4.convertAndSend(queueName4, message);
        System.out.println("sendMessageToServer4-"+message);
    }

    public void sendMessage(String text, Integer receiverId) throws NotFoundException {
        MessageRequest messageRequest = createMessageRequest(text,receiverId);
        String signature = Crypto.signMessageRequest(userService.getPrivateKeyById(messageRequest.getSenderId()),messageRequest);
        String encryptedMessage = Crypto.serializeAndEncryptWithAES(messageRequest);
        List<String> messageParts = Crypto.splitMessageIntoParts(encryptedMessage);
        UUID messageId = UUID.randomUUID();
        MessagePartition lastPartition = new MessagePartition(
                messageId.toString(),
                messageRequest.getSenderId(),
                receiverId,
                signature,
                messageParts.get(messageParts.size()-1),
                messageParts.size(),
                messageParts.size()
        );
        try {
            String serializedPartition = serializeMessagePartition(lastPartition);
            System.out.println("HIDE:"+serializedPartition);
            steganography.hideDataInImage(serializedPartition, messageId.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
        for (int i = 0; i < messageParts.size()-1; i++) {

            MessagePartition partition = new MessagePartition(
                    messageId.toString(),
                    messageRequest.getSenderId(),
                    receiverId,
                    signature,
                    messageParts.get(i),
                    messageParts.size(),
                    i + 1
            );

            String serializedPartition = serializeMessagePartition(partition);
            switch (i % 4) {
                case 0 -> sendMessageToServer1(serializedPartition);
                case 1 -> sendMessageToServer2(serializedPartition);
                case 2 -> sendMessageToServer3(serializedPartition);
                case 3 -> sendMessageToServer4(serializedPartition);
            }
        }

    }

    private String serializeMessagePartition(MessagePartition partition) {
        try {
            return objectMapper.writeValueAsString(partition);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize MessagePartition", e);
        }
    }
    private MessageRequest createMessageRequest(String text, Integer receiverId){
        MessageRequest messageRequest = new MessageRequest();
        messageRequest.setText(text);
        Integer senderId = userService.getCurrentUserId();
        messageRequest.setSenderId(senderId);
        messageRequest.setReceiverId(receiverId);
        return messageRequest;
    }
}
