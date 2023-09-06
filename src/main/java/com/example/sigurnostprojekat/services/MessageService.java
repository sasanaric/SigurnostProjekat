package com.example.sigurnostprojekat.services;

import com.example.sigurnostprojekat.base.CrudService;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.Message;
import java.util.List;

public interface MessageService extends CrudService<Integer> {
    void sendMessage(String text,Integer receiverId) throws NotFoundException;
    List<Message> getUserChatByReceiverId(Integer receiverId);

    Integer getLastMessageIdInChat(Integer senderId);
}
