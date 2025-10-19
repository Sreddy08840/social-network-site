package com.socialnetwork.dao;

import com.socialnetwork.model.Notification;
import com.socialnetwork.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    
    public boolean createNotification(Notification notification) {
        String query = "INSERT INTO notifications (user_id, sender_id, type, message, reference_id, status) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, notification.getUserId());
            stmt.setInt(2, notification.getSenderId());
            stmt.setString(3, notification.getType());
            stmt.setString(4, notification.getMessage());
            stmt.setInt(5, notification.getReferenceId());
            stmt.setString(6, notification.getStatus());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public List<Notification> getNotifications(int userId, int limit, int offset) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT n.*, u.name as sender_name, u.profile_pic as sender_profile_pic " +
                      "FROM notifications n " +
                      "LEFT JOIN users u ON n.sender_id = u.user_id " +
                      "WHERE n.user_id = ? " +
                      "ORDER BY n.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, limit);
            stmt.setInt(3, offset);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public List<Notification> getUnreadNotifications(int userId) {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT n.*, u.name as sender_name, u.profile_pic as sender_profile_pic " +
                      "FROM notifications n " +
                      "LEFT JOIN users u ON n.sender_id = u.user_id " +
                      "WHERE n.user_id = ? AND n.status = 'unread' " +
                      "ORDER BY n.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                notifications.add(extractNotificationFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
    
    public int getUnreadCount(int userId) {
        String query = "SELECT COUNT(*) as count FROM notifications WHERE user_id = ? AND status = 'unread'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public boolean markAsRead(int notificationId) {
        String query = "UPDATE notifications SET status = 'read' WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean markAllAsRead(int userId) {
        String query = "UPDATE notifications SET status = 'read' WHERE user_id = ? AND status = 'unread'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteNotification(int notificationId) {
        String query = "DELETE FROM notifications WHERE notification_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, notificationId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private Notification extractNotificationFromResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setSenderId(rs.getInt("sender_id"));
        notification.setSenderName(rs.getString("sender_name"));
        notification.setSenderProfilePic(rs.getString("sender_profile_pic"));
        notification.setType(rs.getString("type"));
        notification.setMessage(rs.getString("message"));
        notification.setReferenceId(rs.getInt("reference_id"));
        notification.setStatus(rs.getString("status"));
        notification.setCreatedAt(rs.getTimestamp("created_at"));
        return notification;
    }
}
