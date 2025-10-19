package com.socialnetwork.servlet;

import com.google.gson.Gson;
import com.socialnetwork.dao.UserDAO;
import com.socialnetwork.model.User;
import com.socialnetwork.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
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
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        Map<String, Object> jsonResponse = new HashMap<>();
        
        try {
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            
            if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Email and password are required");
                out.print(gson.toJson(jsonResponse));
                return;
            }
            
            User user = userDAO.getUserByEmail(email);
            
            if (user != null && PasswordUtil.checkPassword(password, user.getPassword())) {
                HttpSession session = request.getSession();
                session.setAttribute("userId", user.getUserId());
                session.setAttribute("userName", user.getName());
                session.setAttribute("userEmail", user.getEmail());
                session.setAttribute("userProfilePic", user.getProfilePic());
                
                jsonResponse.put("success", true);
                jsonResponse.put("message", "Login successful");
                jsonResponse.put("redirectUrl", "home.jsp");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "Invalid email or password");
            }
        } catch (Exception e) {
            jsonResponse.put("success", false);
            jsonResponse.put("message", "An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        
        out.print(gson.toJson(jsonResponse));
    }
}
