package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.FriendDAO;
import com.socialnetwork.dao.NotificationDAO;
import com.socialnetwork.dao.UserDAO;
import com.socialnetwork.model.Friend;
import com.socialnetwork.model.Notification;
import com.socialnetwork.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/friend")
public class FriendServlet extends HttpServlet {
    private FriendDAO friendDAO;
    private UserDAO userDAO;
    private NotificationDAO notificationDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        friendDAO = new FriendDAO();
        userDAO = new UserDAO();
        notificationDAO = new NotificationDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        Map<String, Object> jsonResponse = new HashMap<>();
        
        if (session == null || session.getAttribute("userId") == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Not authenticated");
            out.print(gson.toJson(jsonResponse));
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        try {
            if ("list".equals(action)) {
                List<Friend> friends = friendDAO.getFriends(userId);
                jsonResponse.put("success", true);
                jsonResponse.put("friends", friends);
            } else if ("requests".equals(action)) {
                List<Friend> requests = friendDAO.getPendingRequests(userId);
                jsonResponse.put("success", true);
                jsonResponse.put("requests", requests);
            } else if ("status".equals(action)) {
                int friendUserId = Integer.parseInt(request.getParameter("friendUserId"));
                String status = friendDAO.getFriendshipStatus(userId, friendUserId);
                jsonResponse.put("success", true);
                jsonResponse.put("status", status);
            } else if ("search".equals(action)) {
                String searchTerm = request.getParameter("search");
                int page = request.getParameter("page") != null ? 
                          Integer.parseInt(request.getParameter("page")) : 1;
                int limit = 10;
                int offset = (page - 1) * limit;
                
                List<User> users = userDAO.searchUsers(searchTerm, userId, limit, offset);
                jsonResponse.put("success", true);
                jsonResponse.put("users", users);
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        HttpSession session = request.getSession(false);
        Map<String, Object> jsonResponse = new HashMap<>();
        
        if (session == null || session.getAttribute("userId") == null) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Not authenticated");
            out.print(gson.toJson(jsonResponse));
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        String action = request.getParameter("action");
        
        try {
            if ("send_request".equals(action)) {
                sendFriendRequest(request, userId, jsonResponse);
            } else if ("accept".equals(action)) {
                acceptFriendRequest(request, userId, jsonResponse);
            } else if ("remove".equals(action)) {
                removeFriend(request, userId, jsonResponse);
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    private void sendFriendRequest(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int friendUserId = Integer.parseInt(request.getParameter("friendUserId"));
        
        if (userId == friendUserId) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "You cannot send a friend request to yourself");
            return;
        }
        
        // Check if friendship already exists
        String status = friendDAO.getFriendshipStatus(userId, friendUserId);
        if (!"none".equals(status)) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Friend request already exists");
            return;
        }
        
        if (friendDAO.sendFriendRequest(userId, friendUserId)) {
            // Create notification
            User sender = userDAO.getUserById(userId);
            Notification notification = new Notification(
                friendUserId, 
                userId, 
                "friend_request", 
                sender.getName() + " sent you a friend request", 
                0
            );
            notificationDAO.createNotification(notification);
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Friend request sent");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to send friend request");
        }
    }
    
    private void acceptFriendRequest(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int friendUserId = Integer.parseInt(request.getParameter("friendUserId"));
        
        if (friendDAO.acceptFriendRequest(userId, friendUserId)) {
            // Create notification
            User accepter = userDAO.getUserById(userId);
            Notification notification = new Notification(
                friendUserId, 
                userId, 
                "friend_accept", 
                accepter.getName() + " accepted your friend request", 
                0
            );
            notificationDAO.createNotification(notification);
            
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Friend request accepted");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to accept friend request");
        }
    }
    
    private void removeFriend(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int friendUserId = Integer.parseInt(request.getParameter("friendUserId"));
        
        if (friendDAO.removeFriend(userId, friendUserId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Friend removed");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to remove friend");
        }
    }
}
