package com.socialnetwork.model;

import java.sql.Timestamp;

public class Friend {
    private int friendId;
    private int userId;
    private int friendUserId;
    private String friendName;
    private String friendEmail;
    private String friendProfilePic;
    private String status; // pending, accepted, blocked
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Friend() {
    }
    
    public Friend(int friendId, int userId, int friendUserId, String status) {
        this.friendId = friendId;
        this.userId = userId;
        this.friendUserId = friendUserId;
        this.status = status;
    }
    
    // Getters and Setters
    public int getFriendId() {
        return friendId;
    }
    
    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getFriendUserId() {
        return friendUserId;
    }
    
    public void setFriendUserId(int friendUserId) {
        this.friendUserId = friendUserId;
    }
    
    public String getFriendName() {
        return friendName;
    }
    
    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }
    
    public String getFriendEmail() {
        return friendEmail;
    }
    
    public void setFriendEmail(String friendEmail) {
        this.friendEmail = friendEmail;
    }
    
    public String getFriendProfilePic() {
        return friendProfilePic;
    }
    
    public void setFriendProfilePic(String friendProfilePic) {
        this.friendProfilePic = friendProfilePic;
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
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
