# Booking API Documentation

## Overview
The Booking API provides comprehensive functionality for managing desk and resource bookings in the ResourceReserve system. It supports single and recurring bookings with conflict detection and user authentication.

## Base URL
```
http://localhost:3001/api/bookings
```

## Authentication
Most endpoints support JWT authentication via the `Authorization` header:
```
Authorization: Bearer <jwt_token>
```

## Endpoints

### 1. Create Booking
**POST** `/api/bookings/seat`

Creates a new booking for a desk or resource. Supports single and recurring bookings.

#### Request Body
```json
{
  "id": "booking_123",
  "date": "2024-01-15",
  "startTime": "09:00",
  "endTime": "17:00",
  "bookType": "DESK",
  "subType": "DESK_A1",
  "officeLocation": "IN10",
  "building": "Campus 30",
  "floor": "Floor 8",
  "recurrence": {
    "type": "WEEKLY",
    "endDate": "2024-02-15",
    "customDates": null
  },
  "notes": "Booking for project work",
  "userId": "user123"
}
```

#### Recurrence Types
- `NONE`: Single booking (default)
- `DAILY`: Daily recurring booking
- `WEEKLY`: Weekly recurring booking
- `CUSTOM`: Custom dates specified in `customDates` array

#### Response
```json
{
  "success": true,
  "message": "Booking successful",
  "data": {
    "bookings": [
      {
        "id": "booking_123_2024-01-15",
        "date": "2024-01-15",
        "startTime": "09:00",
        "endTime": "17:00",
        "bookType": "DESK",
        "subType": "DESK_A1",
        "officeLocation": "IN10",
        "building": "Campus 30",
        "floor": "Floor 8",
        "recurrenceType": "WEEKLY",
        "endDate": "2024-02-15",
        "customDates": null,
        "status": "confirmed",
        "userId": "user123",
        "notes": "Booking for project work",
        "createdAt": "2024-01-15 09:00:00",
        "updatedAt": "2024-01-15 09:00:00"
      }
    ],
    "totalBookings": 1,
    "bookingDates": ["2024-01-15"]
  }
}
```

#### Error Responses
- **400 Bad Request**: Invalid request data
- **409 Conflict**: Booking conflict detected
- **500 Internal Server Error**: Server error

### 2. Get Bookings
**GET** `/api/bookings`

Retrieves bookings based on various filters.

#### Query Parameters
- `userId` (optional): Filter by user ID
- `seatId` (optional): Filter by seat/resource ID
- `date` (optional): Filter by specific date
- `status` (optional): Filter by booking status

#### Examples
```
GET /api/bookings?userId=user123
GET /api/bookings?seatId=DESK_A1
GET /api/bookings?date=2024-01-15
GET /api/bookings?status=confirmed
```

#### Response
```json
{
  "success": true,
  "message": "Bookings retrieved successfully",
  "data": {
    "bookings": [
      {
        "id": "booking_123_2024-01-15",
        "date": "2024-01-15",
        "startTime": "09:00",
        "endTime": "17:00",
        "bookType": "DESK",
        "subType": "DESK_A1",
        "officeLocation": "IN10",
        "building": "Campus 30",
        "floor": "Floor 8",
        "recurrenceType": "WEEKLY",
        "endDate": "2024-02-15",
        "customDates": null,
        "status": "confirmed",
        "userId": "user123",
        "notes": "Booking for project work",
        "createdAt": "2024-01-15 09:00:00",
        "updatedAt": "2024-01-15 09:00:00"
      }
    ],
    "count": 1
  }
}
```

### 3. Get Booking by ID
**GET** `/api/bookings/{bookingId}`

Retrieves a specific booking by its ID.

#### Response
```json
{
  "success": true,
  "message": "Booking retrieved successfully",
  "data": {
    "booking": {
      "id": "booking_123_2024-01-15",
      "date": "2024-01-15",
      "startTime": "09:00",
      "endTime": "17:00",
      "bookType": "DESK",
      "subType": "DESK_A1",
      "officeLocation": "IN10",
      "building": "Campus 30",
      "floor": "Floor 8",
      "recurrenceType": "WEEKLY",
      "endDate": "2024-02-15",
      "customDates": null,
      "status": "confirmed",
      "userId": "user123",
      "notes": "Booking for project work",
      "createdAt": "2024-01-15 09:00:00",
      "updatedAt": "2024-01-15 09:00:00"
    }
  }
}
```

### 4. Cancel Booking
**DELETE** `/api/bookings/{bookingId}`

Cancels a booking. Requires authentication and authorization.

#### Headers
```
Authorization: Bearer <jwt_token>
```

#### Response
```json
{
  "success": true,
  "message": "Booking cancelled successfully",
  "data": {
    "bookingId": "booking_123_2024-01-15",
    "booking": {
      "id": "booking_123_2024-01-15",
      "date": "2024-01-15",
      "startTime": "09:00",
      "endTime": "17:00",
      "bookType": "DESK",
      "subType": "DESK_A1",
      "officeLocation": "IN10",
      "building": "Campus 30",
      "floor": "Floor 8",
      "recurrenceType": "WEEKLY",
      "endDate": "2024-02-15",
      "customDates": null,
      "status": "cancelled",
      "userId": "user123",
      "notes": "Booking for project work",
      "createdAt": "2024-01-15 09:00:00",
      "updatedAt": "2024-01-15 10:30:00"
    }
  }
}
```

#### Error Responses
- **404 Not Found**: Booking not found
- **403 Forbidden**: Not authorized to cancel booking
- **500 Internal Server Error**: Server error

### 5. Get Bookings by Location
**GET** `/api/bookings/location`

Retrieves bookings for a specific location and date.

#### Query Parameters
- `officeLocation` (required): Office location code
- `building` (required): Building name
- `floor` (required): Floor name
- `date` (required): Date in YYYY-MM-DD format

#### Example
```
GET /api/bookings/location?officeLocation=IN10&building=Campus%2030&floor=Floor%208&date=2024-01-15
```

#### Response
```json
{
  "success": true,
  "message": "Bookings retrieved successfully",
  "data": {
    "bookings": [
      {
        "id": "booking_123_2024-01-15",
        "date": "2024-01-15",
        "startTime": "09:00",
        "endTime": "17:00",
        "bookType": "DESK",
        "subType": "DESK_A1",
        "officeLocation": "IN10",
        "building": "Campus 30",
        "floor": "Floor 8",
        "recurrenceType": "WEEKLY",
        "endDate": "2024-02-15",
        "customDates": null,
        "status": "confirmed",
        "userId": "user123",
        "notes": "Booking for project work",
        "createdAt": "2024-01-15 09:00:00",
        "updatedAt": "2024-01-15 09:00:00"
      }
    ],
    "count": 1
  }
}
```

### 6. Check Seat Availability
**GET** `/api/bookings/check-availability`

Checks if a seat is available for a specific time slot.

#### Query Parameters
- `seatId` (required): Seat/resource ID
- `date` (required): Date in YYYY-MM-DD format
- `startTime` (required): Start time in HH:mm format
- `endTime` (required): End time in HH:mm format

#### Example
```
GET /api/bookings/check-availability?seatId=DESK_A1&date=2024-01-15&startTime=09:00&endTime=17:00
```

#### Response
```json
{
  "success": true,
  "message": "Availability check completed",
  "data": {
    "seatId": "DESK_A1",
    "date": "2024-01-15",
    "startTime": "09:00",
    "endTime": "17:00",
    "isAvailable": false
  }
}
```

## Data Models

### BookingRequest
```json
{
  "id": "string",
  "date": "string (YYYY-MM-DD)",
  "startTime": "string (HH:mm)",
  "endTime": "string (HH:mm)",
  "bookType": "DESK | RESOURCE",
  "subType": "string",
  "officeLocation": "string",
  "building": "string",
  "floor": "string",
  "recurrence": {
    "type": "NONE | DAILY | WEEKLY | CUSTOM",
    "endDate": "string (YYYY-MM-DD)",
    "customDates": ["string (YYYY-MM-DD)"]
  },
  "notes": "string",
  "userId": "string"
}
```

### BookingResponse
```json
{
  "id": "string",
  "date": "string (YYYY-MM-DD)",
  "startTime": "string (HH:mm)",
  "endTime": "string (HH:mm)",
  "bookType": "DESK | RESOURCE",
  "subType": "string",
  "officeLocation": "string",
  "building": "string",
  "floor": "string",
  "recurrenceType": "NONE | DAILY | WEEKLY | CUSTOM",
  "endDate": "string (YYYY-MM-DD)",
  "customDates": ["string (YYYY-MM-DD)"],
  "status": "string",
  "userId": "string",
  "notes": "string",
  "createdAt": "string (YYYY-MM-DD HH:mm:ss)",
  "updatedAt": "string (YYYY-MM-DD HH:mm:ss)"
}
```

## Enums

### BookType
- `DESK`: Desk booking
- `RESOURCE`: Resource booking (meeting rooms, projectors, etc.)

### RecurrenceType
- `NONE`: Single booking
- `DAILY`: Daily recurring
- `WEEKLY`: Weekly recurring
- `CUSTOM`: Custom dates

## Error Handling

All endpoints return consistent error responses:

```json
{
  "success": false,
  "message": "Error description",
  "data": null
}
```

Common HTTP status codes:
- `200 OK`: Success
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required
- `403 Forbidden`: Not authorized
- `404 Not Found`: Resource not found
- `409 Conflict`: Booking conflict
- `500 Internal Server Error`: Server error

## Integration with Floor Plan System

The booking system integrates with the floor plan system by:
1. Using the same location parameters (officeLocation, building, floor)
2. Supporting seat IDs that match the floor plan data
3. Providing availability checking for real-time seat status

## Testing Examples

### Create a Single Booking
```bash
curl -X POST http://localhost:8080/api/bookings/seat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_token>" \
  -d '{
    "id": "test_booking_1",
    "date": "2024-01-15",
    "startTime": "09:00",
    "endTime": "17:00",
    "bookType": "DESK",
    "subType": "DESK_A1",
    "officeLocation": "IN10",
    "building": "Campus 30",
    "floor": "Floor 8",
    "notes": "Test booking"
  }'
```

### Create a Weekly Recurring Booking
```bash
curl -X POST http://localhost:8080/api/bookings/seat \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <jwt_token>" \
  -d '{
    "id": "test_booking_2",
    "date": "2024-01-15",
    "startTime": "09:00",
    "endTime": "17:00",
    "bookType": "DESK",
    "subType": "DESK_A2",
    "officeLocation": "IN10",
    "building": "Campus 30",
    "floor": "Floor 8",
    "recurrence": {
      "type": "WEEKLY",
      "endDate": "2024-02-15"
    },
    "notes": "Weekly recurring booking"
  }'
```

### Check Seat Availability
```bash
curl "http://localhost:8080/api/bookings/check-availability?seatId=DESK_A1&date=2024-01-15&startTime=09:00&endTime=17:00"
```

### Get User's Bookings
```bash
curl "http://localhost:8080/api/bookings?userId=user123" \
  -H "Authorization: Bearer <jwt_token>"
```

### Cancel a Booking
```bash
curl -X DELETE http://localhost:8080/api/bookings/booking_123_2024-01-15 \
  -H "Authorization: Bearer <jwt_token>"
``` 