# UPS Reserve API

A Spring Boot REST API for the UPS Reserve office space booking system, providing authentication, authorization, and user management functionality.

## Features

- **JWT Authentication**: Secure token-based authentication with access and refresh tokens
- **Role-based Authorization**: Admin, Manager, Employee, and Guest roles with granular permissions
- **User Management**: Complete user lifecycle management including registration, profile updates, and password management
- **CORS Support**: Configured for cross-origin requests
- **Database**: Configurable for SQL Server, PostgreSQL, MySQL, or other databases
- **Comprehensive Error Handling**: Standardized error responses with proper HTTP status codes
- **Input Validation**: Request validation with detailed error messages
- **Audit Logging**: Request and error logging for monitoring

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Security** with JWT
- **Spring Data JPA**
- **Database** (configurable - SQL Server, PostgreSQL, MySQL, etc.)
- **Lombok** for boilerplate reduction
- **Maven** for dependency management

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## Quick Start

### 1. Clone and Build

```bash
git clone <repository-url>
cd ResourceReserve
mvn clean install
```

### 2. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:3001/api`

### 3. Database Configuration

Configure your database connection in `application.properties`:

```properties
# For SQL Server
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=upsreserve;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect

# For PostgreSQL
# spring.datasource.url=jdbc:postgresql://localhost:5432/upsreserve
# spring.datasource.driverClassName=org.postgresql.Driver
# spring.datasource.username=postgres
# spring.datasource.password=password
# spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
```

## API Endpoints

### Base URL
```
http://localhost:3001/api
```

### Authentication Endpoints

#### Login
```http
POST /auth/login
Content-Type: application/json

{
  "email": "admin@upsreserve.com",
  "password": "admin123",
  "rememberMe": false
}
```

#### Logout
```http
POST /auth/logout
Authorization: Bearer <access_token>
```

#### Refresh Token
```http
POST /auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

#### Verify Token
```http
GET /auth/verify
Authorization: Bearer <access_token>
```

### User Management Endpoints

#### Get Profile
```http
GET /auth/profile
Authorization: Bearer <access_token>
```

#### Update Profile
```http
PUT /auth/profile
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "name": "John Smith",
  "department": "Engineering",
  "employeeId": "EMP002"
}
```

#### Change Password
```http
POST /auth/change-password
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "currentPassword": "oldpassword123",
  "newPassword": "newpassword123"
}
```

#### Forgot Password
```http
POST /auth/forgot-password
Content-Type: application/json

{
  "email": "admin@upsreserve.com"
}
```

#### Reset Password
```http
POST /auth/reset-password
Content-Type: application/json

{
  "token": "reset_token_here",
  "newPassword": "newpassword123"
}
```

#### Register User (Admin Only)
```http
POST /auth/register
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "email": "newuser@upsreserve.com",
  "password": "password123",
  "name": "Jane Smith",
  "role": "EMPLOYEE",
  "department": "Marketing",
  "employeeId": "EMP003"
}
```

## Default Users

The application creates two default users on startup:

### Admin User
- **Email**: `admin@upsreserve.com`
- **Password**: `admin123`
- **Role**: `ADMIN`
- **Permissions**: Full system access

### Employee User
- **Email**: `employee@upsreserve.com`
- **Password**: `employee123`
- **Role**: `EMPLOYEE`
- **Permissions**: Basic booking access

## User Roles and Permissions

### Admin
- Full system access
- Can manage users, seats, floors, and bookings
- Has `admin:all` permission

### Manager
- Department-level access
- Can manage seats, floors, and bookings
- Cannot delete seats or floors

### Employee
- Basic booking access
- Can read seats and floors
- Can create and read bookings

### Guest
- Read-only access
- Can view seats, floors, and bookings
- Cannot make changes

## Testing the API

### Using cURL

#### 1. Login
```bash
curl -X POST http://localhost:3001/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@upsreserve.com",
    "password": "admin123"
  }'
```

#### 2. Get Profile (with token)
```bash
curl -X GET http://localhost:3001/api/auth/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

#### 3. Update Profile
```bash
curl -X PUT http://localhost:3001/api/auth/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Smith",
    "department": "Engineering"
  }'
```

### Using JavaScript/Fetch

#### Login
```javascript
const loginResponse = await fetch('http://localhost:3001/api/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
  },
  body: JSON.stringify({
    email: 'admin@upsreserve.com',
    password: 'admin123'
  })
});

const loginData = await loginResponse.json();
localStorage.setItem('token', loginData.token);
```

#### Authenticated Request
```javascript
const profileResponse = await fetch('http://localhost:3001/api/auth/profile', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('token')}`,
    'Content-Type': 'application/json',
  }
});

const profileData = await profileResponse.json();
```

## Configuration

### Environment Variables

The application can be configured using environment variables or by modifying `application.properties`:

```properties
# Server Configuration
server.port=3001
server.servlet.context-path=/api

# Database Configuration (SQL Server example)
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=upsreserve;encrypt=true;trustServerCertificate=true
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.username=sa
spring.datasource.password=sa
spring.jpa.database-platform=org.hibernate.dialect.SQLServerDialect

# JWT Configuration
jwt.secret=your-super-secret-jwt-key-change-this-in-production
jwt.refresh-secret=your-super-secret-refresh-key-change-this-in-production
jwt.expiration=3600
jwt.refresh-expiration=604800

# Email Configuration (for password reset)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# CORS Configuration
cors.allowed-origins=https://upsreserve.com,http://localhost:3000
```

## Security Considerations

### JWT Token Configuration
- **Access Token Expiry**: 1 hour (3600 seconds)
- **Refresh Token Expiry**: 7 days (604800 seconds)
- **Algorithm**: HS256
- **Password Encoding**: BCrypt with 12 salt rounds

### Rate Limiting
- **Login attempts**: 5 per 15 minutes per IP
- **Password reset**: 3 per hour per email
- **API requests**: 1000 per hour per user

### CORS Configuration
- Allowed origins: `https://upsreserve.com`, `http://localhost:3000`
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Credentials: enabled

## Error Handling

The API returns standardized error responses:

```json
{
  "success": false,
  "error": "ERROR_TYPE",
  "message": "Detailed error message",
  "code": "ERROR_CODE"
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|-------------|-------------|
| `INVALID_CREDENTIALS` | 401 | Invalid email or password |
| `TOKEN_EXPIRED` | 401 | Access token has expired |
| `TOKEN_INVALID` | 401 | Invalid or malformed token |
| `INSUFFICIENT_PERMISSIONS` | 403 | User lacks required permissions |
| `USER_NOT_FOUND` | 404 | User not found |
| `EMAIL_ALREADY_EXISTS` | 409 | Email address already registered |
| `VALIDATION_ERROR` | 422 | Request validation failed |
| `INTERNAL_SERVER_ERROR` | 500 | Internal server error |

## Development

### Project Structure

```
src/main/java/com/example/ResourceReserve/
├── config/                 # Configuration classes
├── controller/            # REST controllers
├── dto/                   # Data Transfer Objects
├── entity/                # JPA entities
├── exception/             # Exception handlers
├── repository/            # Data access layer
├── security/              # Security configuration
└── service/               # Business logic
```

### Adding New Endpoints

1. Create DTOs in the `dto` package
2. Add service methods in the appropriate service class
3. Create controller endpoints
4. Add proper validation and error handling
5. Update security configuration if needed

### Database Schema

The application uses JPA entities with automatic schema generation:

- **users**: User accounts and profiles
- **user_permissions**: User permissions mapping
- **refresh_tokens**: JWT refresh tokens

## Production Deployment

### Database
Configure your production database:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/ups_reserve
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
```

### Security
- Change JWT secrets to strong, unique values
- Configure proper CORS origins
- Set up HTTPS
- Configure rate limiting
- Set up monitoring and logging

### Environment
- Set `NODE_ENV=production`
- Configure proper logging levels
- Set up health checks
- Configure backup strategies

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License. r e a d m e  
 #   R e s o u r c e R e s e r v e  
 