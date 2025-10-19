// Load user posts
async function loadUserPosts() {
    const container = document.getElementById('postsContainer');
    container.innerHTML = '<div class="loading-spinner">Loading posts...</div>';
    
    try {
        const response = await fetch(`post?action=user&userId=${VIEW_USER_ID}`);
        const data = await response.json();
        
        if (data.success) {
            container.innerHTML = '';
            
            if (data.posts.length === 0) {
                container.innerHTML = '<div class="card"><p class="text-center text-muted">No posts yet</p></div>';
            } else {
                data.posts.forEach(post => {
                    container.appendChild(createPostElement(post));
                });
            }
        }
    } catch (error) {
        console.error('Error loading posts:', error);
        container.innerHTML = '<div class="card"><p class="text-center text-muted">Error loading posts</p></div>';
    }
}

// Create post element (same as home.js)
function createPostElement(post) {
    const card = document.createElement('div');
    card.className = 'card post-card';
    card.setAttribute('data-post-id', post.postId);
    
    const timeAgo = getTimeAgo(new Date(post.createdAt));
    
    card.innerHTML = `
        <div class="post-header">
            <img src="${post.userProfilePic || 'images/default-avatar.png'}" alt="Profile" class="post-avatar">
            <div class="post-author-info">
                <h4><a href="profile.jsp?userId=${post.userId}">${post.userName}</a></h4>
                <span class="post-time">${timeAgo}</span>
            </div>
        </div>
        <div class="post-content">${escapeHtml(post.content)}</div>
        ${post.image ? `<img src="${post.image}" alt="Post image" class="post-image">` : ''}
        <div class="post-stats">
            <span>${post.likesCount} likes</span>
            <span>${post.commentsCount} comments • ${post.sharesCount} shares</span>
        </div>
        <div class="post-actions">
            <button class="post-action-btn like-btn ${post.likedByCurrentUser ? 'liked' : ''}" onclick="toggleLike(${post.postId})">
                <svg width="20" height="20" fill="currentColor">
                    <use href="#${post.likedByCurrentUser ? 'icon-heart-filled' : 'icon-heart'}"></use>
                </svg>
                <span>Like</span>
            </button>
            <button class="post-action-btn" onclick="toggleComments(${post.postId})">
                <svg width="20" height="20" fill="currentColor">
                    <use href="#icon-message"></use>
                </svg>
                <span>Comment</span>
            </button>
            <button class="post-action-btn" onclick="sharePost(${post.postId})">
                <svg width="20" height="20" fill="currentColor">
                    <use href="#icon-share"></use>
                </svg>
                <span>Share</span>
            </button>
        </div>
        <div class="comments-section" id="comments-${post.postId}" style="display: none;">
            <form class="comment-form" onsubmit="addComment(event, ${post.postId})">
                <input type="text" placeholder="Write a comment..." required>
                <button type="submit" class="btn btn-primary btn-sm">Post</button>
            </form>
            <div class="comments-list" id="comments-list-${post.postId}"></div>
        </div>
    `;
    
    return card;
}

// Create Post (if own profile)
const createPostForm = document.getElementById('createPostForm');
if (createPostForm) {
    const postImage = document.getElementById('postImage');
    const selectedFileName = document.getElementById('selectedFileName');
    
    postImage.addEventListener('change', (e) => {
        if (e.target.files.length > 0) {
            selectedFileName.textContent = e.target.files[0].name;
        } else {
            selectedFileName.textContent = '';
        }
    });
    
    createPostForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = new FormData(createPostForm);
        const submitBtn = createPostForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Posting...';
        
        try {
            const response = await fetch('post', {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.success) {
                createPostForm.reset();
                selectedFileName.textContent = '';
                loadUserPosts();
            } else {
                alert(data.message);
            }
        } catch (error) {
            console.error('Error creating post:', error);
            alert('Error creating post');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Post';
        }
    });
}

// Edit Profile Modal
function openEditModal() {
    document.getElementById('editProfileModal').classList.add('show');
}

function closeEditModal() {
    document.getElementById('editProfileModal').classList.remove('show');
}

// Edit Profile Form
const editProfileForm = document.getElementById('editProfileForm');
if (editProfileForm) {
    editProfileForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        
        const formData = new FormData(editProfileForm);
        const submitBtn = editProfileForm.querySelector('button[type="submit"]');
        submitBtn.disabled = true;
        submitBtn.textContent = 'Saving...';
        
        try {
            const response = await fetch('profile', {
                method: 'POST',
                body: formData
            });
            
            const data = await response.json();
            
            if (data.success) {
                alert('Profile updated successfully!');
                location.reload();
            } else {
                const errorMessage = document.getElementById('editErrorMessage');
                errorMessage.textContent = data.message;
                errorMessage.classList.add('show');
            }
        } catch (error) {
            console.error('Error updating profile:', error);
            alert('Error updating profile');
        } finally {
            submitBtn.disabled = false;
            submitBtn.textContent = 'Save Changes';
        }
    });
}

// Friend Action Button (for viewing other profiles)
const friendActionBtn = document.getElementById('friendActionBtn');
if (friendActionBtn && !IS_OWN_PROFILE) {
    loadFriendStatus();
}

async function loadFriendStatus() {
    try {
        const response = await fetch(`friend?action=status&friendUserId=${VIEW_USER_ID}`);
        const data = await response.json();
        
        if (data.success) {
            updateFriendButton(data.status);
        }
    } catch (error) {
        console.error('Error loading friend status:', error);
    }
}

function updateFriendButton(status) {
    const btn = document.getElementById('friendActionBtn');
    
    switch (status) {
        case 'accepted':
            btn.textContent = 'Friends';
            btn.className = 'btn btn-secondary';
            btn.onclick = () => removeFriend();
            break;
        case 'pending':
            btn.textContent = 'Request Sent';
            btn.className = 'btn btn-secondary';
            btn.disabled = true;
            break;
        case 'none':
        default:
            btn.textContent = 'Add Friend';
            btn.className = 'btn btn-primary';
            btn.onclick = () => sendFriendRequest();
            break;
    }
}

async function sendFriendRequest() {
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=send_request&friendUserId=${VIEW_USER_ID}`
        });
        
        const data = await response.json();
        alert(data.message);
        
        if (data.success) {
            updateFriendButton('pending');
        }
    } catch (error) {
        console.error('Error sending friend request:', error);
    }
}

async function removeFriend() {
    if (!confirm('Remove this friend?')) return;
    
    try {
        const response = await fetch('friend', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=remove&friendUserId=${VIEW_USER_ID}`
        });
        
        const data = await response.json();
        alert(data.message);
        
        if (data.success) {
            updateFriendButton('none');
        }
    } catch (error) {
        console.error('Error removing friend:', error);
    }
}

// Post interaction functions (same as home.js)
async function toggleLike(postId) {
    const postCard = document.querySelector(`[data-post-id="${postId}"]`);
    const likeBtn = postCard.querySelector('.like-btn');
    const isLiked = likeBtn.classList.contains('liked');
    const action = isLiked ? 'unlike' : 'like';
    
    try {
        const response = await fetch('post', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=${action}&postId=${postId}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            likeBtn.classList.toggle('liked');
            const icon = likeBtn.querySelector('use');
            icon.setAttribute('href', isLiked ? '#icon-heart' : '#icon-heart-filled');
            
            const likesCount = postCard.querySelector('.post-stats span:first-child');
            const currentCount = parseInt(likesCount.textContent);
            likesCount.textContent = `${isLiked ? currentCount - 1 : currentCount + 1} likes`;
        }
    } catch (error) {
        console.error('Error toggling like:', error);
    }
}

async function toggleComments(postId) {
    const commentsSection = document.getElementById(`comments-${postId}`);
    const commentsList = document.getElementById(`comments-list-${postId}`);
    
    if (commentsSection.style.display === 'none') {
        commentsSection.style.display = 'block';
        
        if (commentsList.children.length === 0) {
            await loadComments(postId);
        }
    } else {
        commentsSection.style.display = 'none';
    }
}

async function loadComments(postId) {
    const commentsList = document.getElementById(`comments-list-${postId}`);
    commentsList.innerHTML = '<div class="loading-spinner" style="padding: 20px;">Loading comments...</div>';
    
    try {
        const response = await fetch(`comment?postId=${postId}`);
        const data = await response.json();
        
        if (data.success) {
            commentsList.innerHTML = '';
            data.comments.forEach(comment => {
                commentsList.appendChild(createCommentElement(comment));
            });
        }
    } catch (error) {
        console.error('Error loading comments:', error);
        commentsList.innerHTML = '<p class="text-muted">Error loading comments</p>';
    }
}

function createCommentElement(comment) {
    const commentDiv = document.createElement('div');
    commentDiv.className = 'comment';
    
    const timeAgo = getTimeAgo(new Date(comment.createdAt));
    
    commentDiv.innerHTML = `
        <img src="${comment.userProfilePic || 'images/default-avatar.png'}" alt="Profile" class="comment-avatar">
        <div class="comment-content">
            <div class="comment-author">${comment.userName}</div>
            <div class="comment-text">${escapeHtml(comment.content)}</div>
            <div class="comment-time">${timeAgo}</div>
        </div>
    `;
    
    return commentDiv;
}

async function addComment(event, postId) {
    event.preventDefault();
    
    const form = event.target;
    const input = form.querySelector('input');
    const content = input.value.trim();
    
    if (!content) return;
    
    const submitBtn = form.querySelector('button');
    submitBtn.disabled = true;
    
    try {
        const response = await fetch('comment', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `postId=${postId}&content=${encodeURIComponent(content)}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            input.value = '';
            await loadComments(postId);
            
            const postCard = document.querySelector(`[data-post-id="${postId}"]`);
            const commentsCount = postCard.querySelector('.post-stats span:last-child');
            const matches = commentsCount.textContent.match(/(\d+) comments/);
            if (matches) {
                const count = parseInt(matches[1]) + 1;
                commentsCount.textContent = commentsCount.textContent.replace(/\d+ comments/, `${count} comments`);
            }
        } else {
            alert(data.message);
        }
    } catch (error) {
        console.error('Error adding comment:', error);
        alert('Error adding comment');
    } finally {
        submitBtn.disabled = false;
    }
}

async function sharePost(postId) {
    if (!confirm('Share this post?')) return;
    
    try {
        const response = await fetch('post', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: `action=share&postId=${postId}`
        });
        
        const data = await response.json();
        
        if (data.success) {
            const postCard = document.querySelector(`[data-post-id="${postId}"]`);
            const sharesCount = postCard.querySelector('.post-stats span:last-child');
            const matches = sharesCount.textContent.match(/(\d+) shares/);
            if (matches) {
                const count = parseInt(matches[1]) + 1;
                sharesCount.textContent = sharesCount.textContent.replace(/\d+ shares/, `${count} shares`);
            }
        }
    } catch (error) {
        console.error('Error sharing post:', error);
    }
}

// Utility Functions
function getTimeAgo(date) {
    const seconds = Math.floor((new Date() - date) / 1000);
    
    if (seconds < 60) return 'Just now';
    if (seconds < 3600) return Math.floor(seconds / 60) + ' minutes ago';
    if (seconds < 86400) return Math.floor(seconds / 3600) + ' hours ago';
    if (seconds < 604800) return Math.floor(seconds / 86400) + ' days ago';
    
    return date.toLocaleDateString();
}

function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
}

// Initialize
loadUserPosts();
