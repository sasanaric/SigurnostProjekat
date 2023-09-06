package com.example.sigurnostprojekat.services.implementation;


import com.example.sigurnostprojekat.crypto.Crypto;
import com.example.sigurnostprojekat.crypto.Steganography;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.Message;
import com.example.sigurnostprojekat.models.dto.MessagePartition;
import com.example.sigurnostprojekat.models.request.MessageRequest;
import com.example.sigurnostprojekat.services.MessageService;
import com.example.sigurnostprojekat.services.UserService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ReceiveMessageService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserService userService;
    private final Object lock = new Object();
    @Autowired
    private MessageService messageService;

    private final Map<String, List<MessagePartition>> mapMessages = new HashMap<>();
    private final Steganography steganography;

    public ReceiveMessageService() {
        this.steganography = new Steganography();
    }


    @RabbitListener(queues = "${rabbitmq.queue1.name}", containerFactory = "factory1")
    public void receiveFromServer1(String message) {
        System.out.println("Listener for Server1 triggered.");
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        MessagePartition messagePart = deserializeMessage(messageBytes);
        processMessagePartFromServer("Server1", messagePart);
    }

    @RabbitListener(queues = "${rabbitmq.queue2.name}", containerFactory = "factory2")
    public void receiveFromServer2(String message) {
        System.out.println("Listener for Server2 triggered.");
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        MessagePartition messagePart = deserializeMessage(messageBytes);
        processMessagePartFromServer("Server2", messagePart);
    }

    @RabbitListener(queues = "${rabbitmq.queue3.name}", containerFactory = "factory3")
    public void receiveFromServer3(String message) {
        System.out.println("Listener for Server3 triggered.");
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        MessagePartition messagePart = deserializeMessage(messageBytes);
        processMessagePartFromServer("Server3", messagePart);
    }

    @RabbitListener(queues = "${rabbitmq.queue4.name}", containerFactory = "factory4")
    public void receiveFromServer4(String message) {
        System.out.println("Listener for Server4 triggered.");
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        MessagePartition messagePart = deserializeMessage(messageBytes);
        processMessagePartFromServer("Server4", messagePart);
    }

    private MessagePartition deserializeMessage(byte[] messageBytes) {
        try {
            return objectMapper.readValue(messageBytes, MessagePartition.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize message", e);
        }
    }

    private void processMessagePartFromServer(String serverName, MessagePartition messagePart){
        System.out.println("Received message part " + messagePart.getMessagePart() + " from " + serverName);
        putMessagePartIntoMap(messagePart);

        List<MessagePartition> messageParts = mapMessages.get(messagePart.getId());

        if (messageParts != null && (messageParts.size() == (messagePart.getTotalParts()-1))) {
            System.out.println("All message parts are received!");
            String assembledEncryptedMessageRequest = assembleFullMessage(messageParts);

            MessageRequest decryptedMessageRequest = Crypto.decryptAndDeserializeWithAES(assembledEncryptedMessageRequest);
            try {
                if (!Crypto.verifyMessageSignature(
                        userService.getPublicKeyById(decryptedMessageRequest.getSenderId()),
                        decryptedMessageRequest,
                        messagePart.getSignature()
                )) {
                    System.out.println("Signature verification failed!");
                }else {
                    System.out.println("Verification OK");
                    processMessage(decryptedMessageRequest);
                }
            } catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void processMessage(MessageRequest messageRequest) throws NotFoundException {
        System.out.println("MESSAGE RECEIVED: "+messageRequest);
        messageService.insert(messageRequest, Message.class);
    }

    public void putMessagePartIntoMap(MessagePartition messagePart) {
        synchronized (lock){
            mapMessages
                    .computeIfAbsent(messagePart.getId(), k -> new ArrayList<>())
                    .add(messagePart);
        }
    }
    private String assembleFullMessage(List<MessagePartition> messageParts) {
        try{
            String lastPartString = steganography.retrieveDataFromImage(messageParts.get(0).getId());
            byte[] lastPartBytes = lastPartString.getBytes(StandardCharsets.UTF_8);
            MessagePartition messagePart = deserializeMessage(lastPartBytes);
            System.out.println(messagePart);
            messageParts.add(messagePart);
            System.out.println("EXTRATCT:"+lastPartString);
            StringBuilder fullMessageContent = new StringBuilder();
            messageParts.sort(Comparator.comparingInt(MessagePartition::getCurrentPart));
            System.out.println("LIST");
            for (MessagePartition part : messageParts) {
                System.out.println(part.getMessagePart());
                fullMessageContent.append(part.getMessagePart());
            }
            return fullMessageContent.toString();
        }catch (Exception e){e.printStackTrace();}
        return "";
    }
}
