package com.example.sigurnostprojekat.controllers;

import com.example.sigurnostprojekat.base.CrudController;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.Message;
import com.example.sigurnostprojekat.models.request.MessageRequest;
import com.example.sigurnostprojekat.services.MessageService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController extends CrudController<Integer, MessageRequest, Message> {
    private final MessageService service;
    MessageController(MessageService service){
        super(Message.class,service);
        this.service = service;
    }

    @RequestMapping("/send/{receiverId}")
    public void insert(@RequestBody MessageRequest request, @PathVariable Integer receiverId) throws NotFoundException {
        service.sendMessage(request.getText(), receiverId);
    }

    @RequestMapping("/user/{receiverId}")
    public List<Message> getChat(@PathVariable Integer receiverId){
        return service.getUserChatByReceiverId(receiverId);
    }

    @RequestMapping("/user/last-message/{receiverId}")
    public Integer getLastMessageId(@PathVariable Integer receiverId){
        return service.getLastMessageIdInChat(receiverId);
    }
}
