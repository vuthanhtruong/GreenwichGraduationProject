# Greenwich University Management System

A comprehensive web-based university management system developed as a graduation project for FPT University Hanoi. This system provides a complete solution for managing academic operations, student information, class schedules, attendance, financial transactions, and communication between students, lecturers, staff, and administrators.

## 📋 Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Deployment](#deployment)
- [User Roles](#user-roles)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)

## 🎯 Overview

The Greenwich University Management System is a full-stack web application designed to streamline and automate various administrative and academic processes within a university environment. The system supports multiple user roles, each with specific permissions and access levels, enabling efficient management of students, lecturers, classes, timetables, attendance, financial transactions, and more.

### Key Objectives

- **Centralized Management**: Single platform for all university operations
- **Role-Based Access Control**: Secure access based on user roles
- **Real-Time Communication**: WebSocket-based messaging system
- **Financial Integration**: Stripe payment gateway for tuition and deposits
- **Automated Notifications**: Email service for important updates
- **Comprehensive Reporting**: Academic transcripts, attendance reports, and financial histories

## ✨ Features

### 1. User Management
- **Multi-Role Support**: Admin, Staff, Deputy Staff, Major Lecturer, Minor Lecturer, Student, Parent
- **User Authentication**: Traditional login and OAuth2 (Google) integration
- **Profile Management**: Personal information, avatar upload, password reset
- **Account Balance**: Track student account balances and transactions

### 2. Academic Management
- **Class Management**: 
  - Major Classes
  - Minor Classes
  - Specialized Classes
- **Subject Management**:
  - Major Subjects
  - Minor Subjects
  - Specialized Subjects
- **Curriculum Management**: Create and manage academic curriculums
- **Syllabus Management**: Detailed syllabus for each subject
- **Study Plans**: Academic roadmap and study plans
- **Academic Transcripts**: 
  - Major Academic Transcripts
  - Minor Academic Transcripts
  - Specialized Academic Transcripts

### 3. Attendance System
- **Major Attendance**: Track attendance for major classes
- **Minor Attendance**: Track attendance for minor classes
- **Specialized Attendance**: Track attendance for specialized classes
- **Attendance Reports**: Generate and view attendance statistics

### 4. Timetable Management
- **Major Timetable**: Schedule management for major classes
- **Minor Timetable**: Schedule management for minor classes
- **Specialized Timetable**: Schedule management for specialized classes
- **Room Management**: Assign and manage classrooms and online rooms

### 5. Communication Features
- **Real-Time Messaging**: WebSocket-based chat system
- **Class Posts**: Announcements and posts for classes
- **News System**: University-wide news and announcements
- **Support Tickets**: Ticket system for student and staff support
- **Email Notifications**: Automated email service for important updates

### 6. Financial Management
- **Tuition Management**: Track tuition fees by campus and year
- **Payment Processing**: Stripe integration for secure payments
- **Deposit Management**: Handle student deposits
- **Financial History**: Complete transaction history
- **Scholarship Management**: Manage scholarships and student scholarships

### 7. Document Management
- **Document Upload**: Upload and manage academic documents
- **Assignment Submissions**: Submit and grade assignments
- **Submission Feedback**: Provide feedback on student submissions

### 8. Additional Features
- **Campus Management**: Manage multiple campuses
- **Major Management**: Academic major administration
- **Specialization Management**: Subject specializations
- **Lecturer Evaluations**: Student feedback on lecturers
- **Retake Subjects**: Manage subject retakes
- **Email Templates**: Customizable email templates
- **Comments System**: Comments on posts and assignments

## 🛠 Technology Stack

### Backend
- **Java 21**: Modern Java features and performance
- **Spring Boot 3.3.5**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Database abstraction layer
- **Hibernate**: ORM framework
- **MySQL 8.4.0**: Relational database
- **WebSocket**: Real-time communication
- **OAuth2 Client**: Google authentication integration

### Frontend
- **Thymeleaf**: Server-side templating engine
- **HTML5/CSS3**: Modern web standards
- **JavaScript**: Client-side interactivity
- **Font Awesome 6.5.1**: Icon library

### Third-Party Integrations
- **Stripe API**: Payment processing
- **Gmail SMTP**: Email service
- **Google OAuth2**: Social authentication

### Development Tools
- **Maven**: Build and dependency management
- **Lombok**: Reduce boilerplate code
- **MapStruct**: Object mapping
- **Spring DevTools**: Development utilities

### Deployment
- **Docker**: Containerization
- **Render.com**: Cloud hosting platform

## 📁 Project Structure

```
GreenwichGraduationProject/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── academicTranscript/      # Academic transcript management
│   │   │   ├── accountBalance/          # Account balance tracking
│   │   │   ├── attendance/              # Attendance system
│   │   │   ├── authenticator/           # Authentication logic
│   │   │   ├── campus/                  # Campus management
│   │   │   ├── classes/                 # Class management
│   │   │   ├── comment/                 # Comment system
│   │   │   ├── config/                  # Configuration classes
│   │   │   ├── curriculum/              # Curriculum management
│   │   │   ├── document/                # Document management
│   │   │   ├── email_service/           # Email service
│   │   │   ├── emailTemplates/          # Email templates
│   │   │   ├── entity/                  # Entity classes and enums
│   │   │   ├── financialHistory/        # Financial transaction history
│   │   │   ├── lecturerEvaluations/     # Lecturer evaluation system
│   │   │   ├── lecturers_Classes/       # Lecturer-class relationships
│   │   │   ├── login/                   # Login controllers
│   │   │   ├── major/                   # Major management
│   │   │   ├── majorLecturers_Specializations/  # Lecturer specializations
│   │   │   ├── messages/                # Messaging system
│   │   │   ├── passwordResetToken/      # Password reset functionality
│   │   │   ├── post/                    # Post and news system
│   │   │   ├── retakeSubjects/          # Retake subject management
│   │   │   ├── room/                    # Room management
│   │   │   ├── scholarship/             # Scholarship management
│   │   │   ├── scholarshipByYear/       # Yearly scholarships
│   │   │   ├── security/                # Security configuration
│   │   │   ├── specialization/          # Specialization management
│   │   │   ├── student_scholarship/      # Student scholarship tracking
│   │   │   ├── studentRequiredSubjects/  # Required subjects for students
│   │   │   ├── students_Classes/         # Student-class relationships
│   │   │   ├── subject/                 # Subject management
│   │   │   ├── submission/              # Assignment submissions
│   │   │   ├── submissionFeedback/      # Submission feedback
│   │   │   ├── supportTickets/          # Support ticket system
│   │   │   ├── syllabus/                # Syllabus management
│   │   │   ├── timetable/               # Timetable management
│   │   │   ├── tuitionByYear/           # Tuition by year
│   │   │   └── user/                    # User management
│   │   │       ├── admin/               # Admin users
│   │   │       ├── deputyStaff/         # Deputy staff users
│   │   │       ├── employe/             # Employee users
│   │   │       ├── majorLecturer/       # Major lecturer users
│   │   │       ├── minorLecturer/       # Minor lecturer users
│   │   │       ├── parentAccount/       # Parent account users
│   │   │       ├── person/              # Base person entity
│   │   │       ├── staff/               # Staff users
│   │   │       └── student/            # Student users
│   │   └── resources/
│   │       ├── application.properties    # Development configuration
│   │       ├── application-prod.properties  # Production configuration
│   │       ├── static/                  # Static resources (CSS, JS, images)
│   │       └── templates/               # Thymeleaf templates
│   └── test/
│       └── java/                        # Test classes
├── Dockerfile                           # Docker configuration
├── render.yaml                          # Render deployment config
├── pom.xml                              # Maven configuration
└── README.md                            # This file
```

## 📋 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
- **Maven 3.6+** (or use the included Maven Wrapper)
- **MySQL 8.0+** database server
- **Docker** (optional, for containerized deployment)
- **Git** for version control

### Required Accounts/Services

- **MySQL Database**: Local or cloud-hosted MySQL instance
- **Gmail Account**: For SMTP email service
- **Google Cloud Console**: For OAuth2 credentials
- **Stripe Account**: For payment processing (optional)
- **Render.com Account**: For cloud deployment (optional)

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone <repository-url>
cd GreenwichGraduationProject
```

### 2. Database Setup

Create a MySQL database for the application:

```sql
CREATE DATABASE greenwich_university CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Environment Configuration

Create a `.env` file in the root directory with the following variables:

```env
# Application
APP_NAME=demo
APP_BASE_URL=http://localhost:8080
PORT=8080

# Database
DB_URL=jdbc:mysql://localhost:3306/greenwich_university?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
DB_DRIVER=com.mysql.cj.jdbc.Driver

# Email Configuration (Gmail SMTP)
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password

# Google OAuth2
GOOGLE_CLIENT_ID=your_google_client_id
GOOGLE_CLIENT_SECRET=your_google_client_secret
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google
GOOGLE_SCOPE=openid,profile,email
GOOGLE_AUTH_URI=https://accounts.google.com/o/oauth2/v2/auth
GOOGLE_TOKEN_URI=https://oauth2.googleapis.com/token
GOOGLE_USER_INFO_URI=https://openidconnect.googleapis.com/v1/userinfo
GOOGLE_JWK_URI=https://www.googleapis.com/oauth2/v3/certs
GOOGLE_USERNAME_ATTR=sub

# Stripe (Optional)
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key
STRIPE_SUCCESS_URL=http://localhost:8080/payment/success
STRIPE_CANCEL_URL=http://localhost:8080/payment/cancel
```

### 4. Build the Project

Using Maven Wrapper (recommended):

```bash
# Windows
mvnw.cmd clean install

# Linux/Mac
./mvnw clean install
```

Or using Maven directly:

```bash
mvn clean install
```

## ⚙️ Configuration

### Database Configuration

The application uses HikariCP connection pool. Key settings in `application.properties`:

```properties
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.idle-timeout=300000
```

### JPA/Hibernate Configuration

```properties
spring.jpa.hibernate.ddl-auto=update  # Development
spring.jpa.show-sql=false
```

### File Upload Configuration

```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=55MB
```

### WebSocket Configuration

```properties
spring.websocket.message-size-limit=16384
spring.websocket.send-buffer-size-limit=524288
spring.websocket.send-time-limit=20000
```

## 🏃 Running the Application

### Development Mode

1. **Start MySQL Database**

2. **Run the Application**

   Using Maven Wrapper:
   ```bash
   mvnw.cmd spring-boot:run
   ```

   Or using the JAR file:
   ```bash
   java -jar target/app.jar
   ```

3. **Access the Application**

   Open your browser and navigate to:
   ```
   http://localhost:8080
   ```

### Using Docker

1. **Build Docker Image**

   ```bash
   docker build -t greenwich-university .
   ```

2. **Run Container**

   ```bash
   docker run -p 8080:8080 --env-file .env greenwich-university
   ```

## 🌐 Deployment

### Deploy to Render.com

1. **Push to Git Repository**

   ```bash
   git add .
   git commit -m "Deploy to Render"
   git push origin main
   ```

2. **Configure Render Service**

   - Create a new Web Service on Render
   - Connect your Git repository
   - Set environment variables in Render dashboard
   - Render will automatically detect the `render.yaml` configuration

3. **Database Setup**

   - Create a MySQL database on Render or use an external database
   - Update `DB_URL` in environment variables

### Environment Variables for Production

Ensure all environment variables from the `.env` file are set in your deployment platform, with production-specific values:

- Update `APP_BASE_URL` to your production domain
- Update `GOOGLE_REDIRECT_URI` to production OAuth callback URL
- Update Stripe URLs to production endpoints
- Use production database credentials

## 👥 User Roles

### 1. Admin
- Full system access
- User management (all roles)
- System configuration
- Academic transcript management
- Campus and major management
- Financial oversight

### 2. Staff
- Student and class management
- Timetable management
- Support ticket handling
- Document management
- News and announcements

### 3. Deputy Staff
- Similar to Staff with additional privileges
- Enhanced reporting capabilities
- Department-level management

### 4. Major Lecturer
- Class management for major subjects
- Attendance tracking
- Assignment creation and grading
- Student evaluation
- Class posts and announcements

### 5. Minor Lecturer
- Class management for minor subjects
- Attendance tracking
- Assignment creation and grading
- Student evaluation

### 6. Student
- View personal information and academic records
- Access class materials and assignments
- Submit assignments
- View timetable and attendance
- Access support tickets
- View account balance and payment history
- Communication with lecturers and staff

### 7. Parent
- View child's academic progress
- View attendance records
- View financial information
- Communication with staff

## 🔐 Security

### Authentication Methods

1. **Traditional Login**: Username and password with BCrypt encryption
2. **OAuth2 (Google)**: Social authentication via Google accounts

### Security Features

- **Password Encryption**: BCrypt password hashing
- **Session Management**: Secure session handling with timeout
- **CSRF Protection**: Enabled for all forms
- **Role-Based Access Control**: Spring Security with role-based authorization
- **Secure Cookies**: HttpOnly and secure cookie settings
- **Input Validation**: Server-side validation for all inputs

### Security Configuration

The security configuration is defined in `SecurityConfig.java`:

- Role-based URL access control
- OAuth2 integration
- CSRF protection (disabled for WebSocket endpoints)
- Session management
- Logout handling

## 📊 Database Schema

The application uses a comprehensive database schema with the following main entities:

- **Users**: Admin, Staff, Deputy Staff, Major Lecturer, Minor Lecturer, Student, Parent
- **Academic**: Classes, Subjects, Curriculums, Syllabi, Academic Transcripts
- **Scheduling**: Timetables, Rooms
- **Attendance**: Major, Minor, Specialized Attendance Records
- **Financial**: Account Balances, Tuition, Deposits, Payments, Scholarships
- **Communication**: Messages, Posts, News, Support Tickets, Comments
- **Documents**: Document storage and assignment submissions

Refer to the `ERD_Diagram.jpg` file for the complete Entity Relationship Diagram.

## 🧪 Testing

Run tests using Maven:

```bash
mvnw.cmd test
```

## 📝 API Documentation

### Authentication Endpoints

- `POST /login` - User login
- `GET /logout` - User logout
- `GET /oauth2/authorization/google` - Google OAuth2 login
- `POST /auth/reset-password` - Password reset request

### Student Endpoints

- `GET /student-home/**` - Student dashboard and features
- `GET /api/student-home/**` - Student API endpoints

### Staff Endpoints

- `GET /staff-home/**` - Staff dashboard and features
- `GET /api/staff-home/**` - Staff API endpoints

### Lecturer Endpoints

- `GET /major-lecturer-home/**` - Major lecturer dashboard
- `GET /minor-lecturer-home/**` - Minor lecturer dashboard
- `GET /api/lecturer-home/**` - Lecturer API endpoints

### Admin Endpoints

- `GET /admin-home/**` - Admin dashboard and features
- `GET /api/admin-home/**` - Admin API endpoints

### Common Endpoints

- `GET /classroom/**` - Classroom access
- `GET /messages/**` - Messaging system
- `GET /documents/**` - Document access
- `GET /check-news/**` - News and announcements

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**
   - Verify MySQL is running
   - Check database credentials in `.env`
   - Ensure database exists

2. **Port Already in Use**
   - Change `PORT` in `.env` or `application.properties`
   - Kill the process using the port

3. **OAuth2 Login Fails**
   - Verify Google OAuth2 credentials
   - Check redirect URI matches Google Console configuration
   - Ensure callback URL is accessible

4. **Email Not Sending**
   - Verify Gmail SMTP credentials
   - Use App Password for Gmail (not regular password)
   - Check firewall/network settings

5. **File Upload Fails**
   - Check file size limits in `application.properties`
   - Verify disk space
   - Check file permissions

## 🤝 Contributing

This is a graduation project. For contributions or improvements:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is developed as a graduation project for FPT University Hanoi. All rights reserved.

## 👨‍💻 Author

Developed as part of the COMP-1682 Final Year Project at FPT University Hanoi.

## 🙏 Acknowledgments

- FPT University Hanoi for project requirements
- Spring Boot community for excellent framework
- All open-source contributors whose libraries made this project possible

---

**Note**: This is a comprehensive university management system. Ensure all environment variables are properly configured before running the application. For production deployment, follow security best practices and use secure database credentials.

