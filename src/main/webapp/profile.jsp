<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.socialnetwork.dao.UserDAO" %>
<%@ page import="com.socialnetwork.model.User" %>
<%
    if (session.getAttribute("userId") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    
    Integer currentUserId = (Integer) session.getAttribute("userId");
    String viewUserIdParam = request.getParameter("userId");
    int viewUserId = viewUserIdParam != null ? Integer.parseInt(viewUserIdParam) : currentUserId;
    
    UserDAO userDAO = new UserDAO();
    User user = userDAO.getUserById(viewUserId);
    
    if (user == null) {
        response.sendRedirect("home.jsp");
        return;
    }
    
    boolean isOwnProfile = (viewUserId == currentUserId);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= user.getName() %> - Profile</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
    <%@ include file="includes/navbar.jsp" %>
    
    <div class="container">
        <div class="profile-container">
            <!-- Profile Header -->
            <div class="card profile-header">
                <div class="profile-cover"></div>
                <div class="profile-info">
                    <img src="<%= user.getProfilePic() != null ? user.getProfilePic() : "images/default-avatar.png" %>" 
                         alt="Profile" class="profile-picture" id="profilePicture">
                    <div class="profile-details">
                        <h1 id="profileName"><%= user.getName() %></h1>
                        <p class="text-muted"><%= user.getEmail() %></p>
                        <p id="profileBio"><%= user.getBio() != null ? user.getBio() : "No bio yet" %></p>
                    </div>
                    <div class="profile-actions">
                        <% if (isOwnProfile) { %>
                            <button class="btn btn-primary" onclick="openEditModal()">Edit Profile</button>
                        <% } else { %>
                            <button class="btn btn-primary" id="friendActionBtn" 
                                    data-user-id="<%= viewUserId %>">Loading...</button>
                        <% } %>
                    </div>
                </div>
            </div>
            
            <!-- Profile Content -->
            <div class="profile-content">
                <aside class="profile-sidebar">
                    <div class="card">
                        <h3>About</h3>
                        <div class="about-info">
                            <p><strong>Joined:</strong> <%= user.getCreatedAt() %></p>
                        </div>
                    </div>
                </aside>
                
                <main class="profile-main">
                    <% if (isOwnProfile) { %>
                    <!-- Create Post -->
                    <div class="card create-post-card">
                        <form id="createPostForm" enctype="multipart/form-data">
                            <div class="create-post-header">
                                <img src="<%= user.getProfilePic() != null ? user.getProfilePic() : "images/default-avatar.png" %>" 
                                     alt="Profile" class="post-avatar">
                                <textarea name="content" id="postContent" 
                                        placeholder="What's on your mind?" 
                                        rows="3" required></textarea>
                            </div>
                            <div class="create-post-footer">
                                <label for="postImage" class="btn btn-secondary btn-sm">
                                    Photo
                                </label>
                                <input type="file" id="postImage" name="image" accept="image/*" style="display: none;">
                                <span id="selectedFileName" class="selected-file"></span>
                                <button type="submit" class="btn btn-primary btn-sm">Post</button>
                            </div>
                        </form>
                    </div>
                    <% } %>
                    
                    <!-- Posts -->
                    <div id="postsContainer">
                        <div class="loading-spinner">Loading posts...</div>
                    </div>
                </main>
            </div>
        </div>
    </div>
    
    <!-- Edit Profile Modal -->
    <% if (isOwnProfile) { %>
    <div id="editProfileModal" class="modal">
        <div class="modal-content">
            <span class="close" onclick="closeEditModal()">&times;</span>
            <h2>Edit Profile</h2>
            <form id="editProfileForm" enctype="multipart/form-data">
                <div class="form-group">
                    <label for="editName">Name</label>
                    <input type="text" id="editName" name="name" value="<%= user.getName() %>" required>
                </div>
                
                <div class="form-group">
                    <label for="editBio">Bio</label>
                    <textarea id="editBio" name="bio" rows="4"><%= user.getBio() != null ? user.getBio() : "" %></textarea>
                </div>
                
                <div class="form-group">
                    <label for="editProfilePic">Profile Picture</label>
                    <input type="file" id="editProfilePic" name="profilePic" accept="image/*">
                </div>
                
                <div id="editErrorMessage" class="error-message"></div>
                
                <button type="submit" class="btn btn-primary">Save Changes</button>
            </form>
        </div>
    </div>
    <% } %>
    
    <%@ include file="includes/svg-icons.jsp" %>
    <script>
        const IS_OWN_PROFILE = <%= isOwnProfile %>;
        const VIEW_USER_ID = <%= viewUserId %>;
    </script>
    <script src="js/profile.js"></script>
</body>
</html>
