package com.socialnetwork.model;

import java.sql.Timestamp;

public class Comment {
    private int commentId;
    private int postId;
    private int userId;
    private String userName;
    private String userProfilePic;
    private String content;
    private Timestamp createdAt;
    
    public Comment() {
    }
    
    public Comment(int postId, int userId, String content) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
    }
    
    // Getters and Setters
    public int getCommentId() {
        return commentId;
    }
    
    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }
    
    public int getPostId() {
        return postId;
    }
    
    public void setPostId(int postId) {
        this.postId = postId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserProfilePic() {
        return userProfilePic;
    }
    
    public void setUserProfilePic(String userProfilePic) {
        this.userProfilePic = userProfilePic;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
