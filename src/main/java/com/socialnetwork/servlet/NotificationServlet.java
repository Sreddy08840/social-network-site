package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.NotificationDAO;
import com.socialnetwork.model.Notification;

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

@WebServlet("/notification")
public class NotificationServlet extends HttpServlet {
    private NotificationDAO notificationDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
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
            if ("list".equals(action) || action == null) {
                int page = request.getParameter("page") != null ? 
                          Integer.parseInt(request.getParameter("page")) : 1;
                int limit = 20;
                int offset = (page - 1) * limit;
                
                List<Notification> notifications = notificationDAO.getNotifications(userId, limit, offset);
                jsonResponse.put("success", true);
                jsonResponse.put("notifications", notifications);
                jsonResponse.put("page", page);
            } else if ("unread".equals(action)) {
                List<Notification> notifications = notificationDAO.getUnreadNotifications(userId);
                jsonResponse.put("success", true);
                jsonResponse.put("notifications", notifications);
            } else if ("count".equals(action)) {
                int count = notificationDAO.getUnreadCount(userId);
                jsonResponse.put("success", true);
                jsonResponse.put("count", count);
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
            if ("mark_read".equals(action)) {
                int notificationId = Integer.parseInt(request.getParameter("notificationId"));
                
                if (notificationDAO.markAsRead(notificationId)) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "Notification marked as read");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to mark notification as read");
                }
            } else if ("mark_all_read".equals(action)) {
                if (notificationDAO.markAllAsRead(userId)) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("message", "All notifications marked as read");
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Failed to mark all notifications as read");
                }
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
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
        
        try {
            int notificationId = Integer.parseInt(request.getParameter("notificationId"));
            
            if (notificationDAO.deleteNotification(notificationId)) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Notification deleted");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to delete notification");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
}
