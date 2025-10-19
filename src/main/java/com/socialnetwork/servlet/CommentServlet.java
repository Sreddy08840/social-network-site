package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.CommentDAO;
import com.socialnetwork.model.Comment;

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

@WebServlet("/comment")
public class CommentServlet extends HttpServlet {
    private CommentDAO commentDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        commentDAO = new CommentDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            List<Comment> comments = commentDAO.getCommentsByPostId(postId);
            
            jsonResponse.put("success", true);
            jsonResponse.put("comments", comments);
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
        
        try {
            int postId = Integer.parseInt(request.getParameter("postId"));
            String content = request.getParameter("content");
            
            if (content == null || content.trim().isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Comment cannot be empty");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            Comment comment = new Comment(postId, userId, content);
            int commentId = commentDAO.createComment(comment);
            
            if (commentId > 0) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Comment added successfully");
                jsonResponse.put("commentId", commentId);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to add comment");
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
        
        int userId = (Integer) session.getAttribute("userId");
        
        try {
            int commentId = Integer.parseInt(request.getParameter("commentId"));
            
            if (commentDAO.deleteComment(commentId, userId)) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Comment deleted successfully");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to delete comment");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
}
