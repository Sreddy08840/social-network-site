let currentPage = 1;
let isLoading = false;

// Load notifications
async function loadNotifications(page = 1) {
    if (isLoading) return;
    
    isLoading = true;
    const container = document.getElementById('notificationsContainer');
    
    if (page === 1) {
        container.innerHTML = '<div class="loading-spinner">Loading notifications...</div>';
    }
    
    try {
        const response = await fetch(`notification?action=list&page=${page}`);
        const data = await response.json();
        
        if (data.success) {
            if (page === 1) {
                container.innerHTML = '';
            }
            
            if (data.notifications.length === 0) {
                if (page === 1) {
                    container.innerHTML = '<p class="text-center text-muted">No notifications yet</p>';
                }
                document.getElementById('loadMoreContainer').style.display = 'none';
            } else {
                data.notifications.forEach(notification => {
                    container.appendChild(createNotificationElement(notification));
                });
                
                if (data.notifications.length === 20) {
                    document.getElementById('loadMoreContainer').style.display = 'block';
                } else {
                    document.getElementById('loadMoreContainer').style.display = 'none';
                }
            }
        }
    } catch (error) {
        console.error('Error loading notifications:', error);
        container.innerHTML = '<p class="text-center text-muted">Error loading notifications</p>';
    } finally {
        isLoading = false;
    }
}

// Create notification element
function createNotificationElement(notification) {
    const div = document.createElement('div');
    div.className = `notification-item ${notification.status === 'unread' ? 'unread' : ''}`;
    div.setAttribute('data-notification-id', notification.notificationId);
    div.onclick = () => markAsRead(notification.notificationId);
    
    const timeAgo = getTimeAgo(new Date(notification.createdAt));
    const icon = getNotificationIcon(notification.type);
    
    div.innerHTML = `
        <img src="${notification.senderProfilePic || 'images/default-avatar.png'}" 
             alt="Profile" class="notification-avatar">
        <div class="notification-content">
            <div class="notification-message">
                <strong>${notification.senderName || 'Someone'}</strong> ${notification.message}
            </div>
            <div class="notification-time">${timeAgo}</div>
        </div>
        ${notification.status === 'unread' ? '<div style="width: 8px; height: 8px; background: #1877f2; border-radius: 50%;"></div>' : ''}
    `;
    
    return div;
}

// Get notification icon based on type
function getNotificationIcon(type) {
    const icons = {
        'friend_request': '👥',
        'friend_accept': '✅',
        'like': '❤️',
        'comment': '💬',
        'share': '🔄',
        'post': '📝'
    };
    return icons[type] || '🔔';
}

// Mark notification as read
async function markAsRead(notificationId) {
    try {
        const response = await fetch('notification', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=mark_read&notificationId=${notificationId}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            const notificationItem = document.querySelector(`[data-notification-id="${notificationId}"]`);
            if (notificationItem) {
                notificationItem.classList.remove('unread');
                const badge = notificationItem.querySelector('div[style*="background: #1877f2"]');
                if (badge) {
                    badge.remove();
                }
            }
        }
    } catch (error) {
        console.error('Error marking notification as read:', error);
    }
}

// Mark all as read
const markAllReadBtn = document.getElementById('markAllReadBtn');
if (markAllReadBtn) {
    markAllReadBtn.addEventListener('click', async () => {
        try {
            const response = await fetch('notification', {
                method: 'POST',
                headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                body: 'action=mark_all_read'
            });
            
            const data = await response.json();
            
            if (data.success) {
                document.querySelectorAll('.notification-item.unread').forEach(item => {
                    item.classList.remove('unread');
                });
                alert('All notifications marked as read');
            }
        } catch (error) {
            console.error('Error marking all as read:', error);
        }
    });
}

// Load More
const loadMoreBtn = document.getElementById('loadMoreBtn');
if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', () => {
        currentPage++;
        loadNotifications(currentPage);
    });
}

// Utility function
function getTimeAgo(date) {
    const seconds = Math.floor((new Date() - date) / 1000);
    
    if (seconds < 60) return 'Just now';
    if (seconds < 3600) return Math.floor(seconds / 60) + ' minutes ago';
    if (seconds < 86400) return Math.floor(seconds / 3600) + ' hours ago';
    if (seconds < 604800) return Math.floor(seconds / 86400) + ' days ago';
    
    return date.toLocaleDateString();
}

// Initialize
loadNotifications(1);
