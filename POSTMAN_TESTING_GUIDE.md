# UPS Reserve API - Postman Testing Guide

This guide will help you test all the UPS Reserve API endpoints using the provided Postman collection.

## 📋 Prerequisites

1. **Postman Desktop App** installed on your machine
2. **UPS Reserve API server** running on `http://localhost:3001/api`
3. **Postman Collection**: `UPS_Reserve_API.postman_collection.json`
4. **Postman Environment**: `UPS_Reserve_API_Environment.postman_environment.json`

## 🚀 Setup Instructions

### 1. Import Collection and Environment

1. Open Postman
2. Click **Import** button
3. Import both files:
   - `UPS_Reserve_API.postman_collection.json`
   - `UPS_Reserve_API_Environment.postman_environment.json`

### 2. Select Environment

1. In the top-right corner of Postman, select **"UPS Reserve API - Local Development"** environment
2. Verify that `base_url` is set to `http://localhost:3001/api`

### 3. Start the API Server

```bash
cd ResourceReserve
mvn spring-boot:run
```

Wait for the server to start and you should see:
```
Started ResourceReserveBeApplication in X.XXX seconds
```

## 🧪 Testing Workflow

### Step 1: Authentication Testing

#### 1.1 Login as Admin
1. Navigate to **Authentication** → **Login**
2. Click **Send**
3. **Expected Response (200):**
```json
{
  "success": true,
  "user": {
    "id": "user_123456",
    "email": "admin@upsreserve.com",
    "name": "Admin User",
    "role": "ADMIN",
    "department": "IT",
    "employeeId": "EMP001",
    "permissions": ["seat:read", "seat:write", "admin:all", ...],
    "isActive": true
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600,
  "message": "Login successful"
}
```

**Note**: The test script automatically saves the tokens to environment variables.

#### 1.2 Login as Employee
1. Navigate to **Authentication** → **Login - Employee**
2. Click **Send**
3. **Expected Response (200):** Similar to admin but with employee role and permissions

#### 1.3 Verify Token
1. Navigate to **Authentication** → **Verify Token**
2. Click **Send**
3. **Expected Response (200):**
```json
{
  "valid": true,
  "user": {
    "id": "user_123456",
    "email": "admin@upsreserve.com",
    "role": "ADMIN",
    "permissions": ["seat:read", "seat:write", "admin:all", ...]
  }
}
```

### Step 2: User Management Testing

#### 2.1 Get Profile
1. Navigate to **User Management** → **Get Profile**
2. Click **Send**
3. **Expected Response (200):**
```json
{
  "success": true,
  "data": {
    "id": "user_123456",
    "email": "admin@upsreserve.com",
    "name": "Admin User",
    "role": "ADMIN",
    "department": "IT",
    "employeeId": "EMP001",
    "avatar": "https://example.com/avatar.jpg",
    "permissions": ["seat:read", "seat:write", "admin:all", ...],
    "isActive": true
  },
  "message": "Profile retrieved successfully"
}
```

#### 2.2 Update Profile
1. Navigate to **User Management** → **Update Profile**
2. Modify the request body if needed:
```json
{
  "name": "John Smith Updated",
  "department": "Engineering",
  "employeeId": "EMP002"
}
```
3. Click **Send**
4. **Expected Response (200):** Updated profile data

#### 2.3 Change Password
1. Navigate to **User Management** → **Change Password**
2. Update the request body with current password:
```json
{
  "currentPassword": "admin123",
  "newPassword": "newpassword123"
}
```
3. Click **Send**
4. **Expected Response (200):**
```json
{
  "success": true,
  "message": "Password changed successfully"
}
```

### Step 3: Admin Operations Testing

#### 3.1 Register New User (Admin Only)
1. Navigate to **Admin Operations** → **Register New User**
2. Modify the request body if needed:
```json
{
  "email": "newuser@upsreserve.com",
  "password": "password123",
  "name": "Jane Smith",
  "role": "EMPLOYEE",
  "department": "Marketing",
  "employeeId": "EMP003"
}
```
3. Click **Send**
4. **Expected Response (201):**
```json
{
  "success": true,
  "data": {
    "id": "user_789012",
    "email": "newuser@upsreserve.com",
    "name": "Jane Smith",
    "role": "EMPLOYEE",
    "department": "Marketing",
    "employeeId": "EMP003",
    "permissions": ["seat:read", "booking:read", "booking:write"],
    "isActive": true
  },
  "message": "User registered successfully"
}
```

#### 3.2 Register Manager User
1. Navigate to **Admin Operations** → **Register Manager User**
2. Click **Send**
3. **Expected Response (201):** Manager user created with appropriate permissions

### Step 4: Token Management Testing

#### 4.1 Refresh Token
1. Navigate to **Authentication** → **Refresh Token**
2. Click **Send**
3. **Expected Response (200):**
```json
{
  "success": true,
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

#### 4.2 Logout
1. Navigate to **Authentication** → **Logout**
2. Click **Send**
3. **Expected Response (200):**
```json
{
  "success": true,
  "message": "Logged out successfully"
}
```

### Step 5: Error Testing

#### 5.1 Invalid Credentials
1. Navigate to **Error Testing** → **Login - Invalid Credentials**
2. Click **Send**
3. **Expected Response (401):**
```json
{
  "success": false,
  "error": "Invalid credentials",
  "message": "Email or password is incorrect"
}
```

#### 5.2 Invalid Email Format
1. Navigate to **Error Testing** → **Login - Invalid Email Format**
2. Click **Send**
3. **Expected Response (422):**
```json
{
  "success": false,
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "code": "VALIDATION_ERROR",
  "details": {
    "email": ["Invalid email format"]
  }
}
```

#### 5.3 Access Without Token
1. Navigate to **Error Testing** → **Access Protected Endpoint - No Token**
2. Click **Send**
3. **Expected Response (401):** Unauthorized access

#### 5.4 Employee Access to Admin Endpoint
1. First, login as employee to get employee token
2. Navigate to **Error Testing** → **Register User - Employee Access (Should Fail)**
3. Click **Send**
4. **Expected Response (403):**
```json
{
  "success": false,
  "error": "INSUFFICIENT_PERMISSIONS",
  "message": "Insufficient permissions",
  "code": "INSUFFICIENT_PERMISSIONS"
}
```

## 🔄 Testing Scenarios

### Scenario 1: Complete User Workflow
1. **Login** as admin
2. **Get Profile** to verify user data
3. **Update Profile** with new information
4. **Register** a new employee user
5. **Logout** to end session

### Scenario 2: Token Refresh Workflow
1. **Login** as admin
2. **Verify Token** to check validity
3. **Refresh Token** to get new tokens
4. **Verify Token** again with new token
5. **Logout**

### Scenario 3: Role-Based Access Testing
1. **Login** as admin
2. **Register** a new user (should succeed)
3. **Login** as employee
4. Try to **Register** a new user (should fail)
5. **Get Profile** as employee (should succeed)

### Scenario 4: Error Handling Testing
1. Try **Login** with invalid credentials
2. Try **Login** with invalid email format
3. Access protected endpoint without token
4. Access protected endpoint with invalid token
5. Try admin operation with employee role

## 📊 Expected Response Codes

| Endpoint | Method | Success Code | Error Codes |
|----------|--------|--------------|-------------|
| `/auth/login` | POST | 200 | 401, 422 |
| `/auth/logout` | POST | 200 | 401, 500 |
| `/auth/refresh` | POST | 200 | 401 |
| `/auth/verify` | GET | 200 | 401 |
| `/auth/profile` | GET | 200 | 401, 500 |
| `/auth/profile` | PUT | 200 | 401, 422, 500 |
| `/auth/change-password` | POST | 200 | 400, 401 |
| `/auth/forgot-password` | POST | 200 | 400 |
| `/auth/reset-password` | POST | 200 | 400 |
| `/auth/register` | POST | 201 | 400, 401, 403, 409 |

## 🔧 Troubleshooting

### Common Issues

#### 1. Server Not Running
**Error**: `Could not get any response`
**Solution**: Start the Spring Boot application with `mvn spring-boot:run`

#### 2. Invalid Token
**Error**: `401 Unauthorized`
**Solution**: 
- Re-run the login request to get a fresh token
- Check if the token is properly set in environment variables

#### 3. CORS Issues
**Error**: `CORS policy` errors
**Solution**: The server is configured for CORS, but if issues persist, check the CORS configuration in `application.properties`

#### 4. Database Connection
**Error**: Database-related errors
**Solution**: 
- Verify database configuration in `application.properties`
- Check if your database server is running
- Ensure database credentials are correct

### Environment Variables Check

Before testing, verify these environment variables are set:
- `base_url`: `http://localhost:3001/api`
- `access_token`: (auto-populated after login)
- `refresh_token`: (auto-populated after login)
- `user_id`: (auto-populated after login)
- `user_email`: (auto-populated after login)
- `user_role`: (auto-populated after login)

## 📝 Notes

1. **Token Management**: The collection automatically saves tokens to environment variables after successful login
2. **Role Testing**: Use both admin and employee accounts to test role-based access
3. **Error Testing**: The error testing section helps verify proper error handling
4. **Data Persistence**: Database data persists between server restarts (depending on your database configuration)
5. **Default Users**: Admin (`admin@upsreserve.com`/`admin123`) and Employee (`employee@upsreserve.com`/`employee123`) are created automatically

## 🎯 Next Steps

After completing these tests, you can:
1. **Add more test cases** for edge scenarios
2. **Test with different user roles** (Manager, Guest)
3. **Add performance testing** with multiple concurrent requests
4. **Test with different data sets** (large payloads, special characters)
5. **Automate testing** using Postman's Newman CLI tool

Happy Testing! 🚀 