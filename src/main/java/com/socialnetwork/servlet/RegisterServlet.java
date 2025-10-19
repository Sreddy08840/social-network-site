package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.UserDAO;
import com.socialnetwork.model.User;
import com.socialnetwork.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserDAO userDAO;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        try {
            String name = request.getParameter("name");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validation
            if (name == null || name.isEmpty() || email == null || email.isEmpty() || 
                password == null || password.isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "All fields are required");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Passwords do not match");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            if (password.length() < 6) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Password must be at least 6 characters long");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            // Check if user already exists
            User existingUser = userDAO.getUserByEmail(email);
            if (existingUser != null) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Email already registered");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            // Create new user
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setPassword(PasswordUtil.hashPassword(password));
            newUser.setProfilePic("default-avatar.png");
            newUser.setBio("Hello! I'm new to this social network.");
            
            if (userDAO.createUser(newUser)) {
                // Get the created user to get the user ID
                User createdUser = userDAO.getUserByEmail(email);
                
                // Create session
                HttpSession session = request.getSession();
                session.setAttribute("userId", createdUser.getUserId());
                session.setAttribute("userName", createdUser.getName());
                session.setAttribute("userEmail", createdUser.getEmail());
                session.setAttribute("userProfilePic", createdUser.getProfilePic());
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Registration successful");
                jsonResponse.put("redirectUrl", "home.jsp");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Registration failed. Please try again.");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
}
