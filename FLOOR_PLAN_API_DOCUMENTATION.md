# Floor Plan System API Documentation

## Overview

The Floor Plan System API provides comprehensive management of buildings, floors, desks, and bookings for office space reservation. This API is built with Spring Boot and provides RESTful endpoints with JWT authentication.

## Base URL

```
http://localhost:3001/api
```

## Authentication

All endpoints require JWT authentication. Include the token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

## API Endpoints

### 1. Buildings Management

#### Get All Buildings
```http
GET /buildings
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Tech Campus Building A",
      "address": "123 Innovation Drive",
      "city": "San Francisco",
      "country": "USA",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "message": "Buildings retrieved successfully"
}
```

#### Get Building by ID
```http
GET /buildings/{id}
```

#### Create Building
```http
POST /buildings
Content-Type: application/json

{
  "name": "New Building",
  "address": "456 Tech Street",
  "city": "San Francisco",
  "country": "USA"
}
```

#### Update Building
```http
PUT /buildings/{id}
Content-Type: application/json

{
  "name": "Updated Building Name",
  "address": "456 Tech Street",
  "city": "San Francisco",
  "country": "USA"
}
```

#### Delete Building
```http
DELETE /buildings/{id}
```

#### Get Buildings by City
```http
GET /buildings/city/{city}
```

#### Get Buildings by Country
```http
GET /buildings/country/{country}
```

### 2. Floors Management

#### Get All Floors
```http
GET /floors
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440001",
      "buildingId": "550e8400-e29b-41d4-a716-446655440000",
      "buildingName": "Tech Campus Building A",
      "name": "First Floor",
      "floorNumber": 1,
      "description": "Main workspace floor",
      "isActive": true,
      "deskCount": 25,
      "availableDesks": 18,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "message": "Floors retrieved successfully"
}
```

#### Get Floors by Building
```http
GET /floors/building/{buildingId}
```

#### Get Floor by ID
```http
GET /floors/{id}
```

#### Create Floor
```http
POST /floors
Content-Type: application/json

{
  "buildingId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Second Floor",
  "floorNumber": 2,
  "description": "Executive offices and meeting rooms",
  "isActive": true
}
```

#### Update Floor
```http
PUT /floors/{id}
Content-Type: application/json

{
  "buildingId": "550e8400-e29b-41d4-a716-446655440000",
  "name": "Updated Floor Name",
  "floorNumber": 2,
  "description": "Updated description",
  "isActive": true
}
```

#### Delete Floor
```http
DELETE /floors/{id}
```

#### Get Active Floors
```http
GET /floors/active
```

### 3. Floor Layouts

#### Get Floor Layout
```http
GET /floors/{id}/layout
```

**Response:**
```json
{
  "success": true,
  "data": {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "floorId": "550e8400-e29b-41d4-a716-446655440001",
    "layoutData": {
      "floorName": "First Floor",
      "dimensions": {
        "width": 1200,
        "height": 800
      },
      "zones": [
        {
          "id": "zone-1",
          "name": "Quiet Zone",
          "x": 0,
          "y": 0,
          "width": 400,
          "height": 300,
          "color": "#e3f2fd"
        }
      ],
      "amenities": [
        {
          "id": "kitchen-1",
          "name": "Kitchen",
          "x": 50,
          "y": 350,
          "width": 150,
          "height": 100,
          "type": "kitchen"
        }
      ]
    },
    "version": 1,
    "isActive": true,
    "createdAt": "2024-01-01T00:00:00",
    "updatedAt": "2024-01-01T00:00:00"
  },
  "message": "Floor layout retrieved successfully"
}
```

#### Update Floor Layout
```http
PUT /floors/{id}/layout
Content-Type: application/json

{
  "layoutData": "{\"floorName\":\"Updated Layout\",\"dimensions\":{\"width\":1200,\"height\":800}}",
  "isActive": true
}
```

#### Delete Floor Layout
```http
DELETE /floors/{id}/layout
```

#### Get Latest Floor Layout
```http
GET /floors/{id}/layout/latest
```

### 4. Desks Management

#### Get All Desks
```http
GET /desks
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440003",
      "floorId": "550e8400-e29b-41d4-a716-446655440001",
      "floorName": "First Floor",
      "deskNumber": "1-01",
      "xPosition": 100,
      "yPosition": 100,
      "width": 100,
      "height": 100,
      "status": "AVAILABLE",
      "deskType": "STANDARD",
      "equipment": "{\"monitor\":true,\"keyboard\":true,\"phone\":false}",
      "isActive": true,
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "message": "Desks retrieved successfully"
}
```

#### Get Desks by Floor
```http
GET /desks/floor/{floorId}
```

#### Get Desks by Status
```http
GET /desks/status/{status}
```

**Status values:** `AVAILABLE`, `OCCUPIED`, `RESERVED`, `MAINTENANCE`

#### Get Desks by Type
```http
GET /desks/type/{deskType}
```

**Desk type values:** `STANDARD`, `STANDING`, `COLLABORATIVE`

#### Get Available Desks by Floor
```http
GET /desks/floor/{floorId}/available
```

#### Get Desk by ID
```http
GET /desks/{id}
```

#### Create Desk
```http
POST /desks
Content-Type: application/json

{
  "floorId": "550e8400-e29b-41d4-a716-446655440001",
  "deskNumber": "1-25",
  "xPosition": 500,
  "yPosition": 300,
  "width": 100,
  "height": 100,
  "status": "AVAILABLE",
  "deskType": "STANDARD",
  "equipment": "{\"monitor\":true,\"keyboard\":true,\"phone\":true}",
  "isActive": true
}
```

#### Update Desk
```http
PUT /desks/{id}
Content-Type: application/json

{
  "floorId": "550e8400-e29b-41d4-a716-446655440001",
  "deskNumber": "1-25",
  "xPosition": 500,
  "yPosition": 300,
  "width": 100,
  "height": 100,
  "status": "AVAILABLE",
  "deskType": "STANDARD",
  "equipment": "{\"monitor\":true,\"keyboard\":true,\"phone\":true}",
  "isActive": true
}
```

#### Delete Desk
```http
DELETE /desks/{id}
```

#### Update Desk Status
```http
PUT /desks/{id}/status?status=MAINTENANCE
```

### 5. Bookings Management

#### Get All Bookings
```http
GET /bookings
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440004",
      "deskId": "550e8400-e29b-41d4-a716-446655440003",
      "deskNumber": "1-01",
      "floorId": "550e8400-e29b-41d4-a716-446655440001",
      "floorName": "First Floor",
      "userId": "550e8400-e29b-41d4-a716-446655440005",
      "startTime": "2024-01-15T09:00:00",
      "endTime": "2024-01-15T17:00:00",
      "status": "CONFIRMED",
      "notes": "Working on project X",
      "createdAt": "2024-01-01T00:00:00",
      "updatedAt": "2024-01-01T00:00:00"
    }
  ],
  "message": "Bookings retrieved successfully"
}
```

#### Get Current User's Bookings
```http
GET /bookings/user
```

#### Get Bookings by User
```http
GET /bookings/user/{userId}
```

#### Get Bookings by Desk
```http
GET /bookings/desk/{deskId}
```

#### Get Bookings by Status
```http
GET /bookings/status/{status}
```

**Status values:** `CONFIRMED`, `CANCELLED`, `COMPLETED`

#### Get Bookings by Date Range (User)
```http
GET /bookings/user/{userId}/date-range?startDate=2024-01-15&endDate=2024-01-20
```

#### Get Bookings by Date Range (Floor)
```http
GET /bookings/floor/{floorId}/date-range?startDate=2024-01-15&endDate=2024-01-20
```

#### Get Booking by ID
```http
GET /bookings/{id}
```

#### Create Booking (Current User)
```http
POST /bookings
Content-Type: application/json

{
  "deskId": "550e8400-e29b-41d4-a716-446655440003",
  "startTime": "2024-01-15T09:00:00",
  "endTime": "2024-01-15T17:00:00",
  "notes": "Working on project X"
}
```

#### Create Booking for Specific User
```http
POST /bookings/user/{userId}
Content-Type: application/json

{
  "deskId": "550e8400-e29b-41d4-a716-446655440003",
  "startTime": "2024-01-15T09:00:00",
  "endTime": "2024-01-15T17:00:00",
  "notes": "Working on project X"
}
```

#### Update Booking
```http
PUT /bookings/{id}
Content-Type: application/json

{
  "deskId": "550e8400-e29b-41d4-a716-446655440003",
  "startTime": "2024-01-15T09:00:00",
  "endTime": "2024-01-15T17:00:00",
  "notes": "Updated notes"
}
```

#### Cancel Booking
```http
PUT /bookings/{id}/cancel
```

#### Delete Booking
```http
DELETE /bookings/{id}
```

#### Check Desk Availability
```http
GET /bookings/desk/{deskId}/availability?startTime=2024-01-15T09:00:00&endTime=2024-01-15T17:00:00
```

## Error Responses

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

Common HTTP status codes:
- `200 OK` - Success
- `201 Created` - Resource created successfully
- `400 Bad Request` - Invalid input data
- `401 Unauthorized` - Authentication required
- `403 Forbidden` - Insufficient permissions
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

## Data Models

### Building
- `id` (UUID) - Unique identifier
- `name` (String) - Building name
- `address` (String) - Building address
- `city` (String) - City
- `country` (String) - Country
- `createdAt` (DateTime) - Creation timestamp
- `updatedAt` (DateTime) - Last update timestamp

### Floor
- `id` (UUID) - Unique identifier
- `buildingId` (UUID) - Reference to building
- `buildingName` (String) - Building name
- `name` (String) - Floor name
- `floorNumber` (Integer) - Floor number
- `description` (String) - Floor description
- `isActive` (Boolean) - Active status
- `deskCount` (Long) - Total desk count
- `availableDesks` (Long) - Available desk count
- `createdAt` (DateTime) - Creation timestamp
- `updatedAt` (DateTime) - Last update timestamp

### FloorLayout
- `id` (UUID) - Unique identifier
- `floorId` (UUID) - Reference to floor
- `layoutData` (String) - JSON layout data
- `version` (Integer) - Layout version
- `isActive` (Boolean) - Active status
- `createdAt` (DateTime) - Creation timestamp
- `updatedAt` (DateTime) - Last update timestamp

### Desk
- `id` (UUID) - Unique identifier
- `floorId` (UUID) - Reference to floor
- `floorName` (String) - Floor name
- `deskNumber` (String) - Desk number
- `xPosition` (Integer) - X coordinate
- `yPosition` (Integer) - Y coordinate
- `width` (Integer) - Desk width
- `height` (Integer) - Desk height
- `status` (DeskStatus) - Desk status
- `deskType` (DeskType) - Desk type
- `equipment` (String) - JSON equipment data
- `isActive` (Boolean) - Active status
- `createdAt` (DateTime) - Creation timestamp
- `updatedAt` (DateTime) - Last update timestamp

### Booking
- `id` (UUID) - Unique identifier
- `deskId` (UUID) - Reference to desk
- `deskNumber` (String) - Desk number
- `floorId` (UUID) - Reference to floor
- `floorName` (String) - Floor name
- `userId` (UUID) - Reference to user
- `startTime` (DateTime) - Booking start time
- `endTime` (DateTime) - Booking end time
- `status` (BookingStatus) - Booking status
- `notes` (String) - Booking notes
- `createdAt` (DateTime) - Creation timestamp
- `updatedAt` (DateTime) - Last update timestamp

## Enums

### DeskStatus
- `AVAILABLE` - Desk is available for booking
- `OCCUPIED` - Desk is currently occupied
- `RESERVED` - Desk is reserved
- `MAINTENANCE` - Desk is under maintenance

### DeskType
- `STANDARD` - Standard desk
- `STANDING` - Standing desk
- `COLLABORATIVE` - Collaborative workspace

### BookingStatus
- `CONFIRMED` - Booking is confirmed
- `CANCELLED` - Booking is cancelled
- `COMPLETED` - Booking is completed

## Sample Usage

### 1. Create a Building and Floor
```bash
# Create building
curl -X POST http://localhost:3001/api/buildings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Tech Campus Building A",
    "address": "123 Innovation Drive",
    "city": "San Francisco",
    "country": "USA"
  }'

# Create floor
curl -X POST http://localhost:3001/api/floors \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "buildingId": "BUILDING_ID_FROM_PREVIOUS_RESPONSE",
    "name": "First Floor",
    "floorNumber": 1,
    "description": "Main workspace floor",
    "isActive": true
  }'
```

### 2. Create Desks and Layout
```bash
# Create desk
curl -X POST http://localhost:3001/api/desks \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "floorId": "FLOOR_ID_FROM_PREVIOUS_RESPONSE",
    "deskNumber": "1-01",
    "xPosition": 100,
    "yPosition": 100,
    "width": 100,
    "height": 100,
    "status": "AVAILABLE",
    "deskType": "STANDARD",
    "equipment": "{\"monitor\":true,\"keyboard\":true,\"phone\":false}",
    "isActive": true
  }'

# Update floor layout
curl -X PUT http://localhost:3001/api/floors/FLOOR_ID/layout \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "layoutData": "{\"floorName\":\"First Floor\",\"dimensions\":{\"width\":1200,\"height\":800}}",
    "isActive": true
  }'
```

### 3. Create a Booking
```bash
curl -X POST http://localhost:3001/api/bookings \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "deskId": "DESK_ID_FROM_PREVIOUS_RESPONSE",
    "startTime": "2024-01-15T09:00:00",
    "endTime": "2024-01-15T17:00:00",
    "notes": "Working on project X"
  }'
```

## Notes

1. **Authentication**: All endpoints require valid JWT authentication
2. **Validation**: All input data is validated using Bean Validation
3. **Audit Fields**: All entities include `createdAt` and `updatedAt` timestamps
4. **Soft Deletes**: Deleted resources are marked as inactive rather than physically deleted
5. **Versioning**: Floor layouts support versioning for tracking changes
6. **Conflict Detection**: Booking system prevents double-booking of desks
7. **CORS**: API supports cross-origin requests for frontend integration

## Database Schema

The system uses the following database tables:
- `buildings` - Building information
- `floors` - Floor information with building references
- `floor_layouts` - Floor layout data with versioning
- `desks` - Desk information with positioning and equipment
- `bookings` - Booking information with time ranges
- `users` - User information (existing table)
- `refresh_tokens` - JWT refresh tokens (existing table)

All tables use UUID primary keys and include audit timestamps. 