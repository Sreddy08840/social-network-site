<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("userId") != null) {
        response.sendRedirect("home.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Social Network</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body class="auth-page">
    <div class="auth-container">
        <div class="auth-card">
            <div class="auth-header">
                <h1>Welcome Back</h1>
                <p>Login to your account</p>
            </div>
            
            <form id="loginForm" class="auth-form">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required 
                           placeholder="Enter your email">
                </div>
                
                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required 
                           placeholder="Enter your password">
                </div>
                
                <div id="errorMessage" class="error-message"></div>
                
                <button type="submit" class="btn btn-primary btn-block">
                    <span id="loginText">Login</span>
                    <span id="loginLoader" class="loader" style="display: none;"></span>
                </button>
            </form>
            
            <div class="auth-footer">
                <p>Don't have an account? <a href="register.jsp">Register here</a></p>
            </div>
        </div>
    </div>
    
    <script src="js/auth.js"></script>
</body>
</html>
