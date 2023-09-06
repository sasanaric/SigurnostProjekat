package com.example.sigurnostprojekat.repositories;

import com.example.sigurnostprojekat.models.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MessageEntityRepository extends JpaRepository<MessageEntity,Integer> {
    @Query("SELECT m FROM MessageEntity m WHERE " +
            "(m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
            "(m.senderId = :userId2 AND m.receiverId = :userId1)")
    List<MessageEntity> findMessagesBetweenUsers(Integer userId1, Integer userId2);

    Optional<MessageEntity> findTopByReceiverIdAndSenderIdOrderByIdDesc(Integer userId1, Integer userId2);
}
