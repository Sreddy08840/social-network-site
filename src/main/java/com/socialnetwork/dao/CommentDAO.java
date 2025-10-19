package com.socialnetwork.dao;

import com.socialnetwork.model.Comment;
import com.socialnetwork.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    
    public int createComment(Comment comment) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Insert comment
            String insertQuery = "INSERT INTO comments (post_id, user_id, content) VALUES (?, ?, ?)";
            int commentId = -1;
            
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, comment.getPostId());
                stmt.setInt(2, comment.getUserId());
                stmt.setString(3, comment.getContent());
                
                int affectedRows = stmt.executeUpdate();
                if (affectedRows > 0) {
                    ResultSet generatedKeys = stmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        commentId = generatedKeys.getInt(1);
                    }
                }
            }
            
            // Update comments count
            String updateCount = "UPDATE posts SET comments_count = comments_count + 1 WHERE post_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateCount)) {
                stmt.setInt(1, comment.getPostId());
                stmt.executeUpdate();
            }
            
            conn.commit();
            return commentId;
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
        return -1;
    }
    
    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT c.*, u.name as user_name, u.profile_pic as user_profile_pic " +
                      "FROM comments c " +
                      "JOIN users u ON c.user_id = u.user_id " +
                      "WHERE c.post_id = ? " +
                      "ORDER BY c.created_at ASC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, postId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                comments.add(extractCommentFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }
    
    public boolean deleteComment(int commentId, int userId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Get post_id before deleting
            int postId = -1;
            String getPostIdQuery = "SELECT post_id FROM comments WHERE comment_id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getPostIdQuery)) {
                stmt.setInt(1, commentId);
                stmt.setInt(2, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    postId = rs.getInt("post_id");
                }
            }
            
            if (postId == -1) {
                conn.rollback();
                return false;
            }
            
            // Delete comment
            String deleteQuery = "DELETE FROM comments WHERE comment_id = ? AND user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                stmt.setInt(1, commentId);
                stmt.setInt(2, userId);
                stmt.executeUpdate();
            }
            
            // Update comments count
            String updateCount = "UPDATE posts SET comments_count = GREATEST(comments_count - 1, 0) WHERE post_id = ?";
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
    
    private Comment extractCommentFromResultSet(ResultSet rs) throws SQLException {
        Comment comment = new Comment();
        comment.setCommentId(rs.getInt("comment_id"));
        comment.setPostId(rs.getInt("post_id"));
        comment.setUserId(rs.getInt("user_id"));
        comment.setUserName(rs.getString("user_name"));
        comment.setUserProfilePic(rs.getString("user_profile_pic"));
        comment.setContent(rs.getString("content"));
        comment.setCreatedAt(rs.getTimestamp("created_at"));
        return comment;
    }
}
