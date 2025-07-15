@echo off
echo Testing Booking API endpoints...
echo ==================================

set BASE_URL=http://localhost:3001/api

echo 1. Testing health check endpoint...
curl -X GET "%BASE_URL%/bookings/health" -H "Content-Type: application/json"

echo.
echo 2. Testing create booking endpoint...
curl -X POST "%BASE_URL%/bookings/seat" -H "Content-Type: application/json" -d "{\"id\": \"test_booking_1\", \"date\": \"2024-01-15\", \"startTime\": \"09:00\", \"endTime\": \"17:00\", \"bookType\": \"DESK\", \"subType\": \"DESK_A1\", \"officeLocation\": \"IN10\", \"building\": \"Campus 30\", \"floor\": \"Floor 8\", \"notes\": \"Test booking\"}"

echo.
echo 3. Testing get bookings endpoint...
curl -X GET "%BASE_URL%/bookings" -H "Content-Type: application/json"

echo.
echo 4. Testing seat availability check...
curl -X GET "%BASE_URL%/bookings/check-availability?seatId=DESK_A1&date=2024-01-15&startTime=09:00&endTime=17:00" -H "Content-Type: application/json"

echo.
echo Test completed!
pause 