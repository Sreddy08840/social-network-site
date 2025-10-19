<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    String userName = (String) session.getAttribute("userName");
    String userProfilePic = (String) session.getAttribute("userProfilePic");
    Integer userId = (Integer) session.getAttribute("userId");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Home - Social Network</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="container">
        <div class="main-content">
            <!-- Sidebar -->
            <aside class="sidebar">
                <div class="profile-widget">
                    <img src="<%= userProfilePic != null ? userProfilePic : "images/default-avatar.png" %>" 
                         alt="Profile" class="profile-avatar">
                    <h3><%= userName %></h3>
                    <a href="profile.jsp" class="btn btn-secondary btn-sm">View Profile</a>
                </div>
                
                <div class="menu-widget">
                    <h4>Navigation</h4>
                    <ul class="menu-list">
                        <li><a href="home.jsp" class="active">Home</a></li>
                        <li><a href="profile.jsp">Profile</a></li>
                        <li><a href="notifications.jsp">Notifications</a></li>
                        <li><a href="friends.jsp">Friends</a></li>
                    </ul>
                </div>
            </aside>
            
            <!-- Feed -->
            <main class="feed">
                <!-- Create Post -->
                <div class="card create-post-card">
                    <form id="createPostForm" enctype="multipart/form-data">
                        <div class="create-post-header">
                            <img src="<%= userProfilePic != null ? userProfilePic : "images/default-avatar.png" %>" 
                                 alt="Profile" class="post-avatar">
                            <textarea name="content" id="postContent" 
                                    placeholder="What's on your mind, <%= userName %>?" 
                                    rows="3" required></textarea>
                        </div>
                        <div class="create-post-footer">
                            <label for="postImage" class="btn btn-secondary btn-sm">
                                <svg width="20" height="20" fill="currentColor">
                                    <use href="#icon-image"></use>
                                </svg>
                                Photo
                            </label>
                            <input type="file" id="postImage" name="image" accept="image/*" style="display: none;">
                            <span id="selectedFileName" class="selected-file"></span>
                            <button type="submit" class="btn btn-primary btn-sm">Post</button>
                        </div>
                    </form>
                </div>
                
                <!-- Posts Feed -->
                <div id="postsContainer">
                    <div class="loading-spinner">Loading posts...</div>
                </div>
                
                <div id="loadMoreContainer" style="text-align: center; margin: 20px 0; display: none;">
                    <button id="loadMoreBtn" class="btn btn-secondary">Load More</button>
                </div>
            </main>
            
            <!-- Right Sidebar -->
            <aside class="right-sidebar">
                <div class="widget">
                    <h4>Friend Suggestions</h4>
                    <div id="friendSuggestions">
                        <p class="text-muted">Loading...</p>
                    </div>
                </div>
            </aside>
        </div>
    </div>
    
    <%@ include file="includes/svg-icons.jsp" %>
    <script src="js/home.js"></script>
</body>
</html>
