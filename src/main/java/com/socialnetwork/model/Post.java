package com.socialnetwork.model;

import java.sql.Timestamp;

public class Post {
    private int postId;
    private int userId;
    private String userName;
    private String userProfilePic;
    private String content;
    private String image;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private boolean likedByCurrentUser;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    public Post() {
    }
    
    public Post(int postId, int userId, String content, String image, Timestamp createdAt) {
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.image = image;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
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
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public int getLikesCount() {
        return likesCount;
    }
    
    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }
    
    public int getCommentsCount() {
        return commentsCount;
    }
    
    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }
    
    public int getSharesCount() {
        return sharesCount;
    }
    
    public void setSharesCount(int sharesCount) {
        this.sharesCount = sharesCount;
    }
    
    public boolean isLikedByCurrentUser() {
        return likedByCurrentUser;
    }
    
    public void setLikedByCurrentUser(boolean likedByCurrentUser) {
        this.likedByCurrentUser = likedByCurrentUser;
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
