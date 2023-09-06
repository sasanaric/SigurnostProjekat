package com.example.sigurnostprojekat.models.dto;

public class MessagePartition {
    private final String id;
    private final Integer senderId;
    private final Integer receiverId;
    private final String signature;
    private final String messagePart;
    private final Integer totalParts;
    private final Integer currentPart;

    public MessagePartition(String id, Integer senderId, Integer receiverId, String signature, String messagePart, Integer totalParts, Integer currentPart) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.signature = signature;
        this.messagePart = messagePart;
        this.totalParts = totalParts;
        this.currentPart = currentPart;
    }

    public String getId() {
        return id;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public Integer getReceiverId() {
        return receiverId;
    }

    public String getMessagePart() {
        return messagePart;
    }

    public Integer getTotalParts() {
        return totalParts;
    }

    public Integer getCurrentPart() {
        return currentPart;
    }

    public String getSignature() {
        return signature;
    }

    @Override
    public String toString() {
        return "MessagePartition{" +
                "id='" + id + '\'' +
                ", senderId=" + senderId +
                ", receiverId=" + receiverId +
                ", signature='" + signature + '\'' +
                ", messagePart='" + messagePart + '\'' +
                ", totalParts=" + totalParts +
                ", currentPart=" + currentPart +
                '}';
    }
}
