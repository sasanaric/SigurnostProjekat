package com.example.sigurnostprojekat.services.implementation;

import com.example.sigurnostprojekat.base.CrudJpaService;
import com.example.sigurnostprojekat.exceptions.NotFoundException;
import com.example.sigurnostprojekat.models.dto.Message;
import com.example.sigurnostprojekat.models.entity.MessageEntity;
import com.example.sigurnostprojekat.repositories.MessageEntityRepository;
import com.example.sigurnostprojekat.services.MessageService;
import com.example.sigurnostprojekat.services.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MessageServiceImplementation extends CrudJpaService<MessageEntity,Integer> implements MessageService {
    private final MessageEntityRepository repository;
    private final ModelMapper modelMapper;
    private final UserService userService;
    private final SendMessageService sendMessageService;

    public MessageServiceImplementation(MessageEntityRepository messageEntityRepository,
                                        ModelMapper modelMapper,
                                        UserService userService,
                                        SendMessageService sendMessageService) {
        super(messageEntityRepository, modelMapper, MessageEntity.class);

        this.repository = messageEntityRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.sendMessageService = sendMessageService;
    }


   @Override
    public void sendMessage(String text, Integer receiverId) throws NotFoundException {
        sendMessageService.sendMessage(text,receiverId);
    }

    @Override
    public List<Message> getUserChatByReceiverId(Integer receiverId) {
        Integer currentUserId = userService.getCurrentUserId();
        return repository
                .findMessagesBetweenUsers(currentUserId,receiverId)
                .stream()
                .map(m -> modelMapper.map(m,Message.class))
                .toList();
    }

    @Override
    public Integer getLastMessageIdInChat(Integer senderId) {
        Integer currentUserId = userService.getCurrentUserId();
        return repository
                .findTopByReceiverIdAndSenderIdOrderByIdDesc(currentUserId,senderId)
                .map(MessageEntity::getId)
                .orElse(0);
    }

}
