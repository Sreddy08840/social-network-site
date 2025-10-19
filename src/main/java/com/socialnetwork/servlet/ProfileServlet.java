package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.UserDAO;
import com.socialnetwork.model.User;
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

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;
    private static final String UPLOAD_DIRECTORY = "uploads/profiles";
    private static final int MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        int userId = (Integer) session.getAttribute("userId");
        String viewUserId = request.getParameter("userId");
        
        if (viewUserId != null && !viewUserId.isEmpty()) {
            userId = Integer.parseInt(viewUserId);
        }
        
        User user = userDAO.getUserById(userId);
        request.setAttribute("user", user);
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
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
                handleProfileUpdate(request, userId, jsonResponse);
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
    
    private void handleProfileUpdate(HttpServletRequest request, int userId, Map<String, Object> jsonResponse) {
        String name = request.getParameter("name");
        String bio = request.getParameter("bio");
        
        User user = userDAO.getUserById(userId);
        if (user != null) {
            user.setName(name != null ? name : user.getName());
            user.setBio(bio != null ? bio : user.getBio());
            
            if (userDAO.updateUser(user)) {
                HttpSession session = request.getSession();
                session.setAttribute("userName", user.getName());
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Profile updated successfully");
                jsonResponse.put("user", user);
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to update profile");
            }
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
        String name = null;
        String bio = null;
        
        for (FileItem item : formItems) {
            if (!item.isFormField()) {
                String originalFileName = new File(item.getName()).getName();
                String fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
                fileName = UUID.randomUUID().toString() + fileExtension;
                String filePath = uploadPath + File.separator + fileName;
                File storeFile = new File(filePath);
                item.write(storeFile.toPath());
            } else {
                if (item.getFieldName().equals("name")) {
                    name = item.getString();
                } else if (item.getFieldName().equals("bio")) {
                    bio = item.getString();
                }
            }
        }
        
        User user = userDAO.getUserById(userId);
        if (user != null) {
            if (name != null && !name.isEmpty()) user.setName(name);
            if (bio != null) user.setBio(bio);
            if (fileName != null) user.setProfilePic(UPLOAD_DIRECTORY + "/" + fileName);
            
            if (userDAO.updateUser(user)) {
                HttpSession session = request.getSession();
                session.setAttribute("userName", user.getName());
                session.setAttribute("userProfilePic", user.getProfilePic());
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Profile updated successfully");
                jsonResponse.put("profilePic", user.getProfilePic());
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Failed to update profile");
            }
        }
    }
}
