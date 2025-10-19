let currentPage = 1;
let isLoading = false;

// Load posts
async function loadPosts(page = 1) {
    if (isLoading) return;
    
    isLoading = true;
    const container = document.getElementById('postsContainer');
    
    if (page === 1) {
        container.innerHTML = '<div class="loading-spinner">Loading posts...</div>';
    }
    
    try {
        const response = await fetch(`post?action=feed&page=${page}`);
        const data = await response.json();
        
        if (data.success) {
            if (page === 1) {
                container.innerHTML = '';
            }
            
            if (data.posts.length === 0) {
                if (page === 1) {
                    container.innerHTML = '<div class="card"><p class="text-center text-muted">No posts yet. Start by creating your first post!</p></div>';
                }
                document.getElementById('loadMoreContainer').style.display = 'none';
            } else {
                data.posts.forEach(post => {
                    container.appendChild(createPostElement(post));
                });
                
                if (data.posts.length === 10) {
                    document.getElementById('loadMoreContainer').style.display = 'block';
                } else {
                    document.getElementById('loadMoreContainer').style.display = 'none';
                }
            }
        }
    } catch (error) {
        console.error('Error loading posts:', error);
        container.innerHTML = '<div class="card"><p class="text-center text-muted">Error loading posts</p></div>';
    } finally {
        isLoading = false;
    }
}

// Create post element
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

// Create Post
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
                loadPosts(1);
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

// Toggle Like
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

// Toggle Comments
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

// Load Comments
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

// Create Comment Element
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

// Add Comment
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

// Share Post
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

// Load More Posts
const loadMoreBtn = document.getElementById('loadMoreBtn');
if (loadMoreBtn) {
    loadMoreBtn.addEventListener('click', () => {
        currentPage++;
        loadPosts(currentPage);
    });
}

// Load Friend Suggestions
async function loadFriendSuggestions() {
    const container = document.getElementById('friendSuggestions');
    if (!container) return;
    
    try {
        const response = await fetch('friend?action=search&search=&page=1');
        const data = await response.json();
        
        if (data.success && data.users.length > 0) {
            container.innerHTML = '';
            data.users.slice(0, 5).forEach(user => {
                const userItem = document.createElement('div');
                userItem.className = 'user-item';
                userItem.innerHTML = `
                    <img src="${user.profilePic || 'images/default-avatar.png'}" alt="Profile" class="user-avatar">
                    <div class="user-info">
                        <div class="user-name">${user.name}</div>
                    </div>
                    <button class="btn btn-primary btn-sm" onclick="sendFriendRequest(${user.userId})">Add</button>
                `;
                container.appendChild(userItem);
            });
        } else {
            container.innerHTML = '<p class="text-muted">No suggestions</p>';
        }
    } catch (error) {
        console.error('Error loading friend suggestions:', error);
    }
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
            loadFriendSuggestions();
        }
    } catch (error) {
        console.error('Error sending friend request:', error);
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
loadPosts(1);
loadFriendSuggestions();
