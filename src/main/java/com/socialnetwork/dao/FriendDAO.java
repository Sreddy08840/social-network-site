package com.socialnetwork.dao;

import com.socialnetwork.model.Friend;
import com.socialnetwork.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {
    
    public boolean sendFriendRequest(int userId, int friendUserId) {
        String query = "INSERT INTO friends (user_id, friend_user_id, status) VALUES (?, ?, 'pending')";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, friendUserId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean acceptFriendRequest(int userId, int friendUserId) {
        String query = "UPDATE friends SET status = 'accepted' WHERE user_id = ? AND friend_user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, friendUserId);
            stmt.setInt(2, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean removeFriend(int userId, int friendUserId) {
        String query = "DELETE FROM friends WHERE (user_id = ? AND friend_user_id = ?) OR (user_id = ? AND friend_user_id = ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, friendUserId);
            stmt.setInt(3, friendUserId);
            stmt.setInt(4, userId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Friend> getFriends(int userId) {
        List<Friend> friends = new ArrayList<>();
        String query = "SELECT f.*, u.name as friend_name, u.email as friend_email, u.profile_pic as friend_profile_pic " +
                      "FROM friends f " +
                      "JOIN users u ON (f.friend_user_id = u.user_id) " +
                      "WHERE f.user_id = ? AND f.status = 'accepted' " +
                      "UNION " +
                      "SELECT f.*, u.name as friend_name, u.email as friend_email, u.profile_pic as friend_profile_pic " +
                      "FROM friends f " +
                      "JOIN users u ON (f.user_id = u.user_id) " +
                      "WHERE f.friend_user_id = ? AND f.status = 'accepted'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                friends.add(extractFriendFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return friends;
    }
    
    public List<Friend> getPendingRequests(int userId) {
        List<Friend> requests = new ArrayList<>();
        String query = "SELECT f.*, u.name as friend_name, u.email as friend_email, u.profile_pic as friend_profile_pic " +
                      "FROM friends f " +
                      "JOIN users u ON f.user_id = u.user_id " +
                      "WHERE f.friend_user_id = ? AND f.status = 'pending'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                requests.add(extractFriendFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return requests;
    }
    
    public String getFriendshipStatus(int userId, int friendUserId) {
        String query = "SELECT status FROM friends WHERE " +
                      "(user_id = ? AND friend_user_id = ?) OR (user_id = ? AND friend_user_id = ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, friendUserId);
            stmt.setInt(3, friendUserId);
            stmt.setInt(4, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("status");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "none";
    }
    
    public boolean areFriends(int userId, int friendUserId) {
        String query = "SELECT COUNT(*) as count FROM friends WHERE " +
                      "((user_id = ? AND friend_user_id = ?) OR (user_id = ? AND friend_user_id = ?)) " +
                      "AND status = 'accepted'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, friendUserId);
            stmt.setInt(3, friendUserId);
            stmt.setInt(4, userId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Friend extractFriendFromResultSet(ResultSet rs) throws SQLException {
        Friend friend = new Friend();
        friend.setFriendId(rs.getInt("friend_id"));
        friend.setUserId(rs.getInt("user_id"));
        friend.setFriendUserId(rs.getInt("friend_user_id"));
        friend.setFriendName(rs.getString("friend_name"));
        friend.setFriendEmail(rs.getString("friend_email"));
        friend.setFriendProfilePic(rs.getString("friend_profile_pic"));
        friend.setStatus(rs.getString("status"));
        friend.setCreatedAt(rs.getTimestamp("created_at"));
        friend.setUpdatedAt(rs.getTimestamp("updated_at"));
        return friend;
    }
}
