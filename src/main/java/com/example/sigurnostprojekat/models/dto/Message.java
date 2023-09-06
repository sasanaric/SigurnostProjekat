package com.example.sigurnostprojekat.models.dto;
import lombok.Data;


@Data
public class Message {
    private Integer id;
    private Integer senderId;
    private Integer receiverId;
    private String text;
}
