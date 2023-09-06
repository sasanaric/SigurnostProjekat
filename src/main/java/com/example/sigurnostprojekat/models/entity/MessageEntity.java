package com.example.sigurnostprojekat.models.entity;

import com.example.sigurnostprojekat.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
@Table(name = "message")
public class MessageEntity implements BaseEntity<Integer> {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id@Column(name = "id")
    private Integer id;
    @Basic@Column(name = "text")
    private String text;
    @Basic@Column(name = "sender_id")
    private Integer senderId;
    @Basic@Column(name = "receiver_id")
    private Integer receiverId;
}
