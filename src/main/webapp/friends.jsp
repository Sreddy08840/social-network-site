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
    <title>Friends - Social Network</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="container">
        <div class="friends-container">
            <!-- Search Users -->
            <div class="card">
                <h3>Find Friends</h3>
                <div class="search-box">
                    <input type="text" id="searchInput" placeholder="Search users..." class="form-control">
                    <button id="searchBtn" class="btn btn-primary">Search</button>
                </div>
                <div id="searchResults"></div>
            </div>
            
            <!-- Friend Requests -->
            <div class="card">
                <h3>Friend Requests</h3>
                <div id="friendRequestsContainer">
                    <div class="loading-spinner">Loading requests...</div>
                </div>
            </div>
            
            <!-- Friends List -->
            <div class="card">
                <h3>My Friends</h3>
                <div id="friendsListContainer">
                    <div class="loading-spinner">Loading friends...</div>
                </div>
            </div>
        </div>
    </div>
    
    <%@ include file="includes/svg-icons.jsp" %>
    <script src="js/friends.js"></script>
</body>
</html>
