package com.socialnetwork.model;

import java.sql.Timestamp;

public class Notification {
    private int notificationId;
    private int userId;
    private int senderId;
    private String senderName;
    private String senderProfilePic;
    private String type; // friend_request, friend_accept, like, comment, share, post
    private String message;
    private int referenceId;
    private String status; // unread, read
    private Timestamp createdAt;
    
    public Notification() {
    }
    
    public Notification(int userId, int senderId, String type, String message, int referenceId) {
        this.userId = userId;
        this.senderId = senderId;
        this.type = type;
        this.message = message;
        this.referenceId = referenceId;
        this.status = "unread";
    }
    
    // Getters and Setters
    public int getNotificationId() {
        return notificationId;
    }
    
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderProfilePic() {
        return senderProfilePic;
    }
    
    public void setSenderProfilePic(String senderProfilePic) {
        this.senderProfilePic = senderProfilePic;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getReferenceId() {
        return referenceId;
    }
    
    public void setReferenceId(int referenceId) {
        this.referenceId = referenceId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
