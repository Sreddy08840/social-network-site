<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String currentUserName = (String) session.getAttribute("userName");
    String currentUserProfilePic = (String) session.getAttribute("userProfilePic");
%>
<nav class="navbar">
    <div class="nav-container">
        <div class="nav-brand">
            <a href="home.jsp">
                <h2>Social Network</h2>
            </a>
        </div>
        
        <div class="nav-search">
            <input type="text" placeholder="Search..." class="search-input">
        </div>
        
        <div class="nav-menu">
            <a href="home.jsp" class="nav-link">
                <svg width="24" height="24" fill="currentColor">
                    <use href="#icon-home"></use>
                </svg>
                <span>Home</span>
            </a>
            
            <a href="friends.jsp" class="nav-link">
                <svg width="24" height="24" fill="currentColor">
                    <use href="#icon-users"></use>
                </svg>
                <span>Friends</span>
            </a>
            
            <a href="notifications.jsp" class="nav-link">
                <svg width="24" height="24" fill="currentColor">
                    <use href="#icon-bell"></use>
                </svg>
                <span>Notifications</span>
                <span id="notificationBadge" class="badge" style="display: none;">0</span>
            </a>
            
            <div class="nav-profile">
                <img src="<%= currentUserProfilePic != null ? currentUserProfilePic : "images/default-avatar.png" %>" 
                     alt="Profile" class="nav-avatar" id="navAvatar">
                <div class="dropdown-menu" id="profileDropdown">
                    <a href="profile.jsp" class="dropdown-item">
                        <svg width="20" height="20" fill="currentColor">
                            <use href="#icon-user"></use>
                        </svg>
                        Profile
                    </a>
                    <a href="logout" class="dropdown-item">
                        <svg width="20" height="20" fill="currentColor">
                            <use href="#icon-logout"></use>
                        </svg>
                        Logout
                    </a>
                </div>
            </div>
        </div>
    </div>
</nav>

<script>
// Notification badge update
function updateNotificationBadge() {
    fetch('notification?action=count')
        .then(response => response.json())
        .then(data => {
            if (data.success && data.count > 0) {
                document.getElementById('notificationBadge').textContent = data.count;
                document.getElementById('notificationBadge').style.display = 'inline-block';
            } else {
                document.getElementById('notificationBadge').style.display = 'none';
            }
        })
        .catch(error => console.error('Error fetching notification count:', error));
}

// Profile dropdown
document.getElementById('navAvatar').addEventListener('click', function(e) {
    e.stopPropagation();
    document.getElementById('profileDropdown').classList.toggle('show');
});

document.addEventListener('click', function() {
    document.getElementById('profileDropdown').classList.remove('show');
});

// Update badge on page load and every 30 seconds
updateNotificationBadge();
setInterval(updateNotificationBadge, 30000);
</script>
