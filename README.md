# Social Network Site

A full-featured social networking web application built with Java JSP, Servlets, JDBC, MySQL, HTML, CSS, and JavaScript.

## 📦 Repository

```bash
git clone https://github.com/Sreddy08840/social-network-site.git
cd social-network-site
```


**GitHub:** [https://github.com/Sreddy08840/social-network-site](https://github.com/Sreddy08840/social-network-site)

## Features

### User Authentication
- **Secure Registration & Login**: Password hashing using BCrypt
- **Session Management**: Secure session handling with HTTP-only cookies
- **Profile Management**: Users can edit their profile information and upload profile pictures

### Social Features
- **Posts**: Create, read, update, and delete posts with text and images
- **Likes**: Like and unlike posts with real-time updates
- **Comments**: Comment on posts with threaded discussions
- **Sharing**: Share posts with friends
- **Friend Management**: Send friend requests, accept/decline requests, view friends list
- **Notifications**: Real-time notifications for friend requests, likes, comments, and shares
- **User Search**: Search for users by name or email

### Additional Features
- **Responsive Design**: Mobile-friendly interface using CSS media queries
- **AJAX Integration**: Smooth user experience without page reloads
- **Pagination**: Efficient loading of posts and notifications
- **Modern UI**: Beautiful and intuitive user interface with gradients and animations

## Technology Stack

### Backend
- **Java 11**: Core programming language
- **JSP (JavaServer Pages)**: Dynamic web pages
- **Servlets**: HTTP request handling
- **JDBC**: Database connectivity
- **MySQL 8.0**: Relational database
- **Maven**: Dependency management and build tool

### Frontend
- **HTML5**: Semantic markup
- **CSS3**: Styling with modern features (Grid, Flexbox, CSS Variables)
- **JavaScript (ES6+)**: Client-side interactivity and AJAX
- **Responsive Design**: Mobile-first approach

### Libraries & Tools
- **BCrypt**: Password hashing
- **Gson**: JSON serialization/deserialization
- **Apache Commons FileUpload**: File upload handling
- **Apache Commons IO**: File operations

## Project Structure

```
social-network-site/
├── database/
│   └── schema.sql                      # Database schema and sample data
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── socialnetwork/
│       │           ├── dao/            # Data Access Objects
│       │           │   ├── UserDAO.java
│       │           │   ├── PostDAO.java
│       │           │   ├── FriendDAO.java
│       │           │   ├── NotificationDAO.java
│       │           │   └── CommentDAO.java
│       │           ├── model/          # Model classes
│       │           │   ├── User.java
│       │           │   ├── Post.java
│       │           │   ├── Friend.java
│       │           │   ├── Notification.java
│       │           │   └── Comment.java
│       │           ├── servlet/        # Servlets
│       │           │   ├── LoginServlet.java
│       │           │   ├── RegisterServlet.java
│       │           │   ├── LogoutServlet.java
│       │           │   ├── ProfileServlet.java
│       │           │   ├── PostServlet.java
│       │           │   ├── CommentServlet.java
│       │           │   ├── FriendServlet.java
│       │           │   └── NotificationServlet.java
│       │           └── util/           # Utility classes
│       │               ├── DatabaseConnection.java
│       │               └── PasswordUtil.java
│       └── webapp/
│           ├── WEB-INF/
│           │   └── web.xml             # Deployment descriptor
│           ├── css/
│           │   └── style.css           # Styles
│           ├── js/
│           │   ├── auth.js             # Authentication logic
│           │   ├── home.js             # Home page logic
│           │   ├── profile.js          # Profile page logic
│           │   ├── notifications.js    # Notifications logic
│           │   └── friends.js          # Friends logic
│           ├── images/
│           │   └── default-avatar.png  # Default profile picture
│           ├── includes/
│           │   ├── navbar.jsp          # Navigation bar
│           │   └── svg-icons.jsp       # SVG icons
│           ├── uploads/                # User uploaded files
│           ├── login.jsp               # Login page
│           ├── register.jsp            # Registration page
│           ├── home.jsp                # Home/Feed page
│           ├── profile.jsp             # User profile page
│           ├── notifications.jsp       # Notifications page
│           ├── friends.jsp             # Friends page
│           └── index.jsp               # Landing page
└── pom.xml                             # Maven configuration
```

## Database Schema

### Tables
1. **users**: User accounts and profiles
2. **posts**: User posts with content and images
3. **friends**: Friend relationships
4. **notifications**: User notifications
5. **likes**: Post likes
6. **comments**: Post comments

## Installation & Setup

### Prerequisites
- **Java JDK 11** or higher
- **Apache Tomcat 9.0** or higher
- **MySQL 8.0** or higher
- **Maven 3.6** or higher

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd social-network-site
```

### Step 2: Setup MySQL Database
1. Start MySQL server
2. Login to MySQL:
```bash
mysql -u root -p
```

3. Create database and import schema:
```sql
source database/schema.sql
```

4. Update database credentials in `src/main/java/com/socialnetwork/util/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/social_network?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "your_password";
```

### Step 3: Build the Project
```bash
mvn clean package
```

This will create a WAR file in the `target/` directory named `social-network.war`.

### Step 4: Deploy to Tomcat

#### Option 1: Manual Deployment
1. Copy the WAR file to Tomcat's webapps directory:
```bash
cp target/social-network.war $TOMCAT_HOME/webapps/
```

2. Start Tomcat:
```bash
$TOMCAT_HOME/bin/startup.sh    # Linux/Mac
$TOMCAT_HOME/bin/startup.bat   # Windows
```

#### Option 2: Tomcat Manager
1. Access Tomcat Manager at `http://localhost:8080/manager`
2. Upload the WAR file using the deployment interface

### Step 5: Access the Application
Open your browser and navigate to:
```
http://localhost:8080/social-network/
```

## Default Test Accounts

The database schema includes sample users (password for all is "password123"):

1. **Email**: john@example.com
2. **Email**: jane@example.com
3. **Email**: bob@example.com

## Usage Guide

### Registration
1. Navigate to the registration page
2. Fill in your name, email, and password
3. Click "Register" to create your account

### Login
1. Enter your email and password
2. Click "Login" to access your account

### Creating Posts
1. On the home page, type your message in the "What's on your mind?" box
2. Optionally, click "Photo" to attach an image
3. Click "Post" to publish

### Managing Friends
1. Navigate to the "Friends" page
2. Use the search box to find users
3. Click "Add Friend" to send a friend request
4. Accept or decline incoming requests in the "Friend Requests" section

### Viewing Notifications
1. Click the notification icon in the navigation bar
2. View all your notifications
3. Click "Mark All as Read" to clear unread notifications

## API Endpoints

### Authentication
- `POST /login` - User login
- `POST /register` - User registration
- `GET /logout` - User logout

### Posts
- `GET /post?action=feed&page=1` - Get feed posts
- `GET /post?action=user&userId=X` - Get user posts
- `POST /post` - Create post
- `POST /post?action=like&postId=X` - Like post
- `POST /post?action=unlike&postId=X` - Unlike post
- `DELETE /post?postId=X` - Delete post

### Comments
- `GET /comment?postId=X` - Get post comments
- `POST /comment` - Add comment
- `DELETE /comment?commentId=X` - Delete comment

### Friends
- `GET /friend?action=list` - Get friends list
- `GET /friend?action=requests` - Get friend requests
- `GET /friend?action=search&search=term` - Search users
- `POST /friend?action=send_request` - Send friend request
- `POST /friend?action=accept` - Accept friend request
- `POST /friend?action=remove` - Remove friend

### Notifications
- `GET /notification?action=list&page=1` - Get notifications
- `GET /notification?action=count` - Get unread count
- `POST /notification?action=mark_read` - Mark notification as read
- `POST /notification?action=mark_all_read` - Mark all as read

## Configuration

### Database Configuration
Edit `src/main/java/com/socialnetwork/util/DatabaseConnection.java`:
```java
private static final String URL = "jdbc:mysql://localhost:3306/social_network";
private static final String USER = "your_username";
private static final String PASSWORD = "your_password";
```

### File Upload Configuration
Edit servlet parameters in:
- `ProfileServlet.java` - Profile picture uploads
- `PostServlet.java` - Post image uploads

Maximum file size is set to 5MB for profiles and 10MB for posts.

## Security Features

1. **Password Hashing**: BCrypt with salt rounds
2. **SQL Injection Prevention**: Prepared statements
3. **XSS Protection**: HTML escaping in JavaScript
4. **Session Security**: HTTP-only cookies
5. **CSRF Protection**: Session validation

## Browser Compatibility

- Chrome (recommended)
- Firefox
- Safari
- Edge
- Opera

## Responsive Breakpoints

- **Desktop**: > 1200px
- **Tablet**: 768px - 1199px
- **Mobile**: < 767px

## Troubleshooting

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `DatabaseConnection.java`
- Ensure database `social_network` exists
- Check MySQL port (default: 3306)

### Tomcat Deployment Issues
- Verify Tomcat is running
- Check Tomcat logs in `$TOMCAT_HOME/logs/`
- Ensure no port conflicts (default: 8080)
- Verify WAR file was created successfully

### File Upload Issues
- Check upload directory permissions
- Verify max file size limits
- Ensure sufficient disk space

## Development

### Building for Development
```bash
mvn clean compile
```

### Running Tests
```bash
mvn test
```

### Hot Reload
Use an IDE like Eclipse or IntelliJ IDEA with Tomcat integration for hot reload during development.

## Future Enhancements

- [ ] Real-time chat messaging
- [ ] Video/audio calls
- [ ] Stories feature
- [ ] Groups and communities
- [ ] Advanced search filters
- [ ] Email notifications
- [ ] Two-factor authentication
- [ ] Dark mode
- [ ] Multi-language support
- [ ] Mobile apps (iOS/Android)

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License.

## Support

For issues, questions, or suggestions:
- Create an issue in the repository
- Contact the development team

## Acknowledgments

- Icons from custom SVG library
- Design inspiration from modern social media platforms
- Built with best practices from Java EE specifications

---

**Built with ❤️ using Java, JSP, Servlets, and MySQL**
#   s o c i a l - n e t w o r k - s i t e 
 
 
