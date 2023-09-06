package com.example.sigurnostprojekat.models.request;
import lombok.Data;

@Data
public class MessageRequest {
    private String text;
    private Integer senderId;
    private Integer receiverId;

    @Override
    public String toString() {
        return "MessageRequest {" +
                "text='" + text + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                '}';
    }
}
