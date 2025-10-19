package com.socialnetwork.dao;

import com.socialnetwork.model.Post;
import com.socialnetwork.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    
    public int createPost(Post post) {
        String query = "INSERT INTO posts (user_id, content, image) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getImage());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    public Post getPostById(int postId, int currentUserId) {
        String query = "SELECT p.*, u.name as user_name, u.profile_pic as user_profile_pic, " +
                      "EXISTS(SELECT 1 FROM likes WHERE post_id = p.post_id AND user_id = ?) as liked_by_user " +
                      "FROM posts p JOIN users u ON p.user_id = u.user_id WHERE p.post_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, postId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPostFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Post> getFeedPosts(int userId, int limit, int offset) {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT p.*, u.name as user_name, u.profile_pic as user_profile_pic, " +
                      "EXISTS(SELECT 1 FROM likes WHERE post_id = p.post_id AND user_id = ?) as liked_by_user " +
                      "FROM posts p " +
                      "JOIN users u ON p.user_id = u.user_id " +
                      "WHERE p.user_id IN (SELECT friend_user_id FROM friends WHERE user_id = ? AND status = 'accepted') " +
                      "OR p.user_id IN (SELECT user_id FROM friends WHERE friend_user_id = ? AND status = 'accepted') " +
                      "OR p.user_id = ? " +
                      "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.setInt(3, userId);
            stmt.setInt(4, userId);
            stmt.setInt(5, limit);
            stmt.setInt(6, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(extractPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    public List<Post> getUserPosts(int userId, int currentUserId, int limit, int offset) {
        List<Post> posts = new ArrayList<>();
        String query = "SELECT p.*, u.name as user_name, u.profile_pic as user_profile_pic, " +
                      "EXISTS(SELECT 1 FROM likes WHERE post_id = p.post_id AND user_id = ?) as liked_by_user " +
                      "FROM posts p " +
                      "JOIN users u ON p.user_id = u.user_id " +
                      "WHERE p.user_id = ? " +
                      "ORDER BY p.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUserId);
            stmt.setInt(2, userId);
            stmt.setInt(3, limit);
            stmt.setInt(4, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                posts.add(extractPostFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }
    
    public boolean updatePost(Post post) {
        String query = "UPDATE posts SET content = ?, image = ? WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, post.getContent());
            stmt.setString(2, post.getImage());
            stmt.setInt(3, post.getPostId());
            stmt.setInt(4, post.getUserId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deletePost(int postId, int userId) {
        String query = "DELETE FROM posts WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean likePost(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert like
            String insertLike = "INSERT INTO likes (post_id, user_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertLike)) {
                stmt.setInt(1, postId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            
            // Update likes count
            String updateCount = "UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateCount)) {
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    public boolean unlikePost(int postId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Delete like
            String deleteLike = "DELETE FROM likes WHERE post_id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteLike)) {
                stmt.setInt(1, postId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            
            // Update likes count
            String updateCount = "UPDATE posts SET likes_count = GREATEST(likes_count - 1, 0) WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateCount)) {
                stmt.setInt(1, postId);
                stmt.executeUpdate();
            }
            
            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    public boolean incrementShareCount(int postId) {
        String query = "UPDATE posts SET shares_count = shares_count + 1 WHERE post_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, postId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Post extractPostFromResultSet(ResultSet rs) throws SQLException {
        Post post = new Post();
        post.setPostId(rs.getInt("post_id"));
        post.setUserId(rs.getInt("user_id"));
        post.setUserName(rs.getString("user_name"));
        post.setUserProfilePic(rs.getString("user_profile_pic"));
        post.setContent(rs.getString("content"));
        post.setImage(rs.getString("image"));
        post.setLikesCount(rs.getInt("likes_count"));
        post.setCommentsCount(rs.getInt("comments_count"));
        post.setSharesCount(rs.getInt("shares_count"));
        post.setLikedByCurrentUser(rs.getBoolean("liked_by_user"));
        post.setCreatedAt(rs.getTimestamp("created_at"));
        post.setUpdatedAt(rs.getTimestamp("updated_at"));
        return post;
    }
}
