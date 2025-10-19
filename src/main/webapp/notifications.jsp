<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String userName = (String) session.getAttribute("userName");
    String userProfilePic = (String) session.getAttribute("userProfilePic");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Notifications - Social Network</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="container">
        <div class="notifications-container">
            <div class="card">
                <div class="notifications-header">
                    <h2>Notifications</h2>
                    <button id="markAllReadBtn" class="btn btn-secondary btn-sm">Mark All as Read</button>
                </div>
                
                <div id="notificationsContainer">
                    <div class="loading-spinner">Loading notifications...</div>
                </div>
                
                <div id="loadMoreContainer" style="text-align: center; margin: 20px 0; display: none;">
                    <button id="loadMoreBtn" class="btn btn-secondary">Load More</button>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="includes/svg-icons.jsp" %>
    <script src="js/notifications.js"></script>
</body>
</html>
