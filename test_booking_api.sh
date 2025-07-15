#!/bin/bash

# Test script for Booking API
BASE_URL="http://localhost:3001/api"

echo "Testing Booking API endpoints..."
echo "=================================="

# Test 1: Health check
echo "1. Testing health check endpoint..."
curl -X GET "$BASE_URL/bookings/health" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# Test 2: Create a simple booking
echo "2. Testing create booking endpoint..."
curl -X POST "$BASE_URL/bookings/seat" \
  -H "Content-Type: application/json" \
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
  }' \
  -w "\nHTTP Status: %{http_code}\n\n"

# Test 3: Get all bookings
echo "3. Testing get bookings endpoint..."
curl -X GET "$BASE_URL/bookings" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

# Test 4: Check seat availability
echo "4. Testing seat availability check..."
curl -X GET "$BASE_URL/bookings/check-availability?seatId=DESK_A1&date=2024-01-15&startTime=09:00&endTime=17:00" \
  -H "Content-Type: application/json" \
  -w "\nHTTP Status: %{http_code}\n\n"

echo "Test completed!" 