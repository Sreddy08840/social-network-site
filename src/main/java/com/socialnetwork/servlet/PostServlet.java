package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.NotificationDAO;
import com.socialnetwork.dao.PostDAO;
import com.socialnetwork.model.Notification;
import com.socialnetwork.model.Post;
import org.apache.commons.fileupload2.core.DiskFileItemFactory;
import org.apache.commons.fileupload2.core.FileItem;
import org.apache.commons.fileupload2.jakarta.servlet6.JakartaServletFileUpload;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@WebServlet("/post")
public class PostServlet extends HttpServlet {
    private PostDAO postDAO;
    private Gson gson;
    private static final String UPLOAD_DIRECTORY = "uploads/posts";
    private static final int MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    @Override
    public void init() throws ServletException {
        postDAO = new PostDAO();
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
            if ("feed".equals(action)) {
                int page = request.getParameter("page") != null ? 
                          Integer.parseInt(request.getParameter("page")) : 1;
                int limit = 10;
                int offset = (page - 1) * limit;
                
                List<Post> posts = postDAO.getFeedPosts(userId, limit, offset);
                jsonResponse.put("success", true);
                jsonResponse.put("posts", posts);
                jsonResponse.put("page", page);
            } else if ("user".equals(action)) {
                int targetUserId = Integer.parseInt(request.getParameter("userId"));
                int page = request.getParameter("page") != null ? 
                          Integer.parseInt(request.getParameter("page")) : 1;
                int limit = 10;
                int offset = (page - 1) * limit;
                
                List<Post> posts = postDAO.getUserPosts(targetUserId, userId, limit, offset);
                jsonResponse.put("success", true);
                jsonResponse.put("posts", posts);
            } else if ("single".equals(action)) {
                int postId = Integer.parseInt(request.getParameter("postId"));
                Post post = postDAO.getPostById(postId, userId);
                
                if (post != null) {
                    jsonResponse.put("success", true);
                    jsonResponse.put("post", post);
                } else {
                    jsonResponse.put("success", false);
                    jsonResponse.put("message", "Post not found");
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
            if (JakartaServletFileUpload.isMultipartContent(request)) {
                handleFileUpload(request, userId, jsonResponse);
            } else {
                String action = request.getParameter("action");
                
                if ("create".equals(action)) {
                    createPost(request, userId, jsonResponse);
                } else if ("like".equals(action)) {
                    likePost(request, userId, jsonResponse);
                } else if ("unlike".equals(action)) {
                    unlikePost(request, userId, jsonResponse);
                } else if ("share".equals(action)) {
                    sharePost(request, userId, jsonResponse);
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
        
        int userId = (Integer) session.getAttribute("userId");
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        try {
            if (postDAO.deletePost(postId, userId)) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Post deleted successfully");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to delete post");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    private void createPost(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        String content = request.getParameter("content");
        
        if (content == null || content.trim().isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Post content cannot be empty");
            return;
        }
        
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        
        int postId = postDAO.createPost(post);
        
        if (postId > 0) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Post created successfully");
            jsonResponse.put("postId", postId);
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to create post");
        }
    }
    
    private void handleFileUpload(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) 
            throws Exception {
        DiskFileItemFactory.Builder factoryBuilder = DiskFileItemFactory.builder();
        JakartaServletFileUpload upload = new JakartaServletFileUpload(factoryBuilder.get());
        upload.setFileSizeMax(MAX_FILE_SIZE);
        
        String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIRECTORY;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        List<FileItem> formItems = upload.parseRequest(request);
        String fileName = null;
        String content = null;
        
        for (FileItem item : formItems) {
            if (!item.isFormField()) {
                String originalFileName = new File(item.getName()).getName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                fileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + fileName;
                File storeFile = new File(filePath);
                item.write(storeFile.toPath());
            } else if (item.getFieldName().equals("content")) {
                content = item.getString();
            }
        }
        
        if (content == null || content.trim().isEmpty()) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Post content cannot be empty");
            return;
        }
        
        Post post = new Post();
        post.setUserId(userId);
        post.setContent(content);
        if (fileName != null) {
            post.setImage(UPLOAD_DIRECTORY + "/" + fileName);
        }
        
        int postId = postDAO.createPost(post);
        
        if (postId > 0) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Post created successfully");
            jsonResponse.put("postId", postId);
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to create post");
        }
    }
    
    private void likePost(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        if (postDAO.likePost(postId, userId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Post liked");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to like post");
        }
    }
    
    private void unlikePost(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        if (postDAO.unlikePost(postId, userId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Post unliked");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to unlike post");
        }
    }
    
    private void sharePost(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        int postId = Integer.parseInt(request.getParameter("postId"));
        
        if (postDAO.incrementShareCount(postId)) {
            jsonResponse.put("success", true);
            jsonResponse.put("message", "Post shared");
        } else {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "Failed to share post");
        }
    }
}
