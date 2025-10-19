// Load Friend Requests
async function loadFriendRequests() {
    const container = document.getElementById('friendRequestsContainer');
    container.innerHTML = '<div class="loading-spinner">Loading...</div>';
    
    try {
        const response = await fetch('friend?action=requests');
        const data = await response.json();
        
        if (data.success) {
            container.innerHTML = '';
            
            if (data.requests.length === 0) {
                container.innerHTML = '<p class="text-muted">No pending requests</p>';
            } else {
                data.requests.forEach(request => {
                    container.appendChild(createFriendRequestElement(request));
                });
            }
        }
    } catch (error) {
        console.error('Error loading friend requests:', error);
        container.innerHTML = '<p class="text-muted">Error loading requests</p>';
    }
}

// Create friend request element
function createFriendRequestElement(request) {
    const div = document.createElement('div');
    div.className = 'friend-item';
    
    div.innerHTML = `
        <img src="${request.friendProfilePic || 'images/default-avatar.png'}" 
             alt="Profile" class="friend-avatar">
        <div class="friend-info">
            <div class="friend-name">
                <a href="profile.jsp?userId=${request.userId}">${request.friendName}</a>
            </div>
            <div class="friend-email">${request.friendEmail}</div>
        </div>
        <div class="friend-actions">
            <button class="btn btn-success btn-sm" onclick="acceptRequest(${request.userId})">Accept</button>
            <button class="btn btn-danger btn-sm" onclick="rejectRequest(${request.userId})">Decline</button>
        </div>
    `;
    
    return div;
}

// Accept friend request
async function acceptRequest(friendUserId) {
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=accept&friendUserId=${friendUserId}`
        });
        
        const data = await response.json();
        alert(data.message);
        
        if (data.success) {
            loadFriendRequests();
            loadFriendsList();
        }
    } catch (error) {
        console.error('Error accepting request:', error);
    }
}

// Reject friend request
async function rejectRequest(friendUserId) {
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=remove&friendUserId=${friendUserId}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            loadFriendRequests();
        }
    } catch (error) {
        console.error('Error rejecting request:', error);
    }
}

// Load Friends List
async function loadFriendsList() {
    const container = document.getElementById('friendsListContainer');
    container.innerHTML = '<div class="loading-spinner">Loading...</div>';
    
    try {
        const response = await fetch('friend?action=list');
        const data = await response.json();
        
        if (data.success) {
            container.innerHTML = '';
            
            if (data.friends.length === 0) {
                container.innerHTML = '<p class="text-muted">No friends yet</p>';
            } else {
                data.friends.forEach(friend => {
                    container.appendChild(createFriendElement(friend));
                });
            }
        }
    } catch (error) {
        console.error('Error loading friends:', error);
        container.innerHTML = '<p class="text-muted">Error loading friends</p>';
    }
}

// Create friend element
function createFriendElement(friend) {
    const div = document.createElement('div');
    div.className = 'friend-item';
    
    // Determine which user info to display
    const displayUserId = friend.friendUserId;
    const displayName = friend.friendName;
    const displayEmail = friend.friendEmail;
    const displayPic = friend.friendProfilePic;
    
    div.innerHTML = `
        <img src="${displayPic || 'images/default-avatar.png'}" 
             alt="Profile" class="friend-avatar">
        <div class="friend-info">
            <div class="friend-name">
                <a href="profile.jsp?userId=${displayUserId}">${displayName}</a>
            </div>
            <div class="friend-email">${displayEmail}</div>
        </div>
        <div class="friend-actions">
            <button class="btn btn-secondary btn-sm" onclick="viewProfile(${displayUserId})">View Profile</button>
            <button class="btn btn-danger btn-sm" onclick="unfriend(${displayUserId})">Unfriend</button>
        </div>
    `;
    
    return div;
}

// View profile
function viewProfile(userId) {
    window.location.href = `profile.jsp?userId=${userId}`;
}

// Unfriend
async function unfriend(friendUserId) {
    if (!confirm('Are you sure you want to remove this friend?')) return;
    
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=remove&friendUserId=${friendUserId}`
        });
        
        const data = await response.json();
        alert(data.message);
        
        if (data.success) {
            loadFriendsList();
        }
    } catch (error) {
        console.error('Error unfriending:', error);
    }
}

// Search Users
const searchBtn = document.getElementById('searchBtn');
const searchInput = document.getElementById('searchInput');

if (searchBtn && searchInput) {
    searchBtn.addEventListener('click', searchUsers);
    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            searchUsers();
        }
    });
}

async function searchUsers() {
    const searchTerm = searchInput.value.trim();
    const resultsContainer = document.getElementById('searchResults');
    
    if (!searchTerm) {
        resultsContainer.innerHTML = '';
        return;
    }
    
    resultsContainer.innerHTML = '<div class="loading-spinner" style="padding: 20px;">Searching...</div>';
    
    try {
        const response = await fetch(`friend?action=search&search=${encodeURIComponent(searchTerm)}`);
        const data = await response.json();
        
        if (data.success) {
            resultsContainer.innerHTML = '';
            
            if (data.users.length === 0) {
                resultsContainer.innerHTML = '<p class="text-muted">No users found</p>';
            } else {
                data.users.forEach(user => {
                    resultsContainer.appendChild(createSearchResultElement(user));
                });
            }
        }
    } catch (error) {
        console.error('Error searching users:', error);
        resultsContainer.innerHTML = '<p class="text-muted">Error searching users</p>';
    }
}

// Create search result element
function createSearchResultElement(user) {
    const div = document.createElement('div');
    div.className = 'friend-item';
    
    div.innerHTML = `
        <img src="${user.profilePic || 'images/default-avatar.png'}" 
             alt="Profile" class="friend-avatar">
        <div class="friend-info">
            <div class="friend-name">
                <a href="profile.jsp?userId=${user.userId}">${user.name}</a>
            </div>
            <div class="friend-email">${user.email}</div>
        </div>
        <div class="friend-actions">
            <button class="btn btn-primary btn-sm" onclick="sendFriendRequest(${user.userId})">Add Friend</button>
        </div>
    `;
    
    return div;
}

// Send Friend Request
async function sendFriendRequest(friendUserId) {
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=send_request&friendUserId=${friendUserId}`
        });
        
        const data = await response.json();
        alert(data.message);
        
        if (data.success) {
            searchInput.value = '';
            document.getElementById('searchResults').innerHTML = '';
        }
    } catch (error) {
        console.error('Error sending friend request:', error);
    }
}

// Initialize
loadFriendRequests();
loadFriendsList();
