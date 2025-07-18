package com.example.ResourceReserve.controller;

import com.example.ResourceReserve.dto.ApiResponse;
import com.example.ResourceReserve.dto.BookingRequest;
import com.example.ResourceReserve.dto.BookingResponse;
import com.example.ResourceReserve.entity.Booking;
import com.example.ResourceReserve.entity.Booking.BookType;
import com.example.ResourceReserve.entity.Booking.RecurrenceType;
import com.example.ResourceReserve.repository.BookingRepository;
import com.example.ResourceReserve.repository.UserRepository;
import com.example.ResourceReserve.service.BookingService;
import com.example.ResourceReserve.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/bookings")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingRepository bookingRepo;
    private final BookingService bookingService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse> healthCheck() {
        log.info("Health check endpoint called");
        try {
            // Test database connectivity
            long count = bookingRepo.count();
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Booking service is healthy")
                    .data(Map.of(
                            "status", "UP", 
                            "timestamp", System.currentTimeMillis(),
                            "database", "connected",
                            "bookingCount", count
                    ))
                    .build());
        } catch (Exception e) {
            log.error("Health check failed", e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ApiResponse.builder()
                    .success(false)
                    .message("Service unhealthy: " + e.getMessage())
                    .data(Map.of("status", "DOWN", "timestamp", System.currentTimeMillis()))
                    .build());
        }
    }

    @PostMapping("/seat")
    public ResponseEntity<ApiResponse> createBooking(
            @RequestBody BookingRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        log.info("Received booking request: {}", request);
        log.info("Auth header: {}", authHeader);
        
        try {
            // Extract user ID from JWT token or use from request
            String userId = extractUserId(authHeader, request);
            log.info("Extracted userId: {}", userId);
            
            log.info("Calling bookingService.createBooking...");
            List<Booking> savedBookings = bookingService.createBooking(request, userId);
            log.info("Booking created successfully, count: {}", savedBookings.size());
            
            List<BookingResponse> bookingResponses = savedBookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            List<String> bookingDates = savedBookings.stream()
                    .map(booking -> booking.getDate().toString())
                    .toList();

            log.info("Returning successful response");
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Booking successful")
                    .data(Map.of(
                            "bookings", bookingResponses,
                            "totalBookings", savedBookings.size(),
                            "bookingDates", bookingDates
                    ))
                    .build());

        } catch (IllegalArgumentException e) {
            log.warn("Invalid booking request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (IllegalStateException e) {
            log.warn("Booking conflict: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error creating booking", e);
            String errorMessage = "Internal server error";
            if (e.getMessage() != null && !e.getMessage().contains("rollback")) {
                errorMessage += ": " + e.getMessage();
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message(errorMessage)
                    .build());
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getBookings(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String seatId,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String status) {
        
        try {
            List<Booking> bookings = bookingService.getBookings(userId, seatId, date, status);
            
            List<BookingResponse> bookingResponses = bookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .data(Map.of("bookings", bookingResponses, "count", bookings.size()))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving bookings", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving bookings: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> cancelBooking(
            @PathVariable String bookingId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            String userId = extractUserId(authHeader, null);
            
            Booking cancelledBooking = bookingService.cancelBooking(bookingId, userId);
            
            var userOpt = userRepository.findById(cancelledBooking.getUserId());
            String userName = userOpt.map(u -> u.getName()).orElse(null);
            String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Booking cancelled successfully")
                    .data(Map.of("bookingId", bookingId, "booking", BookingResponse.fromEntity(cancelledBooking, userName, userEmail)))
                    .build());
                    
        } catch (IllegalArgumentException e) {
            log.warn("Booking not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("Not authorized to cancel booking: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        } catch (Exception e) {
            log.error("Error cancelling booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error cancelling booking: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse> getBookingById(@PathVariable String bookingId) {
        try {
            Optional<Booking> booking = bookingService.getBookingById(bookingId);
            
            if (booking.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            var userOpt = userRepository.findById(booking.get().getUserId());
            String userName = userOpt.map(u -> u.getName()).orElse(null);
            String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Booking retrieved successfully")
                    .data(Map.of("booking", BookingResponse.fromEntity(booking.get(), userName, userEmail)))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving booking", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving booking: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/location")
    public ResponseEntity<ApiResponse> getBookingsByLocation(
            @RequestParam String officeLocation,
            @RequestParam String building,
            @RequestParam String floor,
            @RequestParam String date) {
        
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            List<Booking> bookings = bookingService.getBookingsByLocation(officeLocation, building, floor, bookingDate);
            
            List<BookingResponse> bookingResponses = bookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Bookings retrieved successfully")
                    .data(Map.of("bookings", bookingResponses, "count", bookings.size()))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving bookings by location", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving bookings: " + e.getMessage())
                    .build());
        }
    }
    
    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse> checkSeatAvailability(
            @RequestParam String seatId,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        
        try {
            LocalDate bookingDate = LocalDate.parse(date);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);
            
            boolean isAvailable = !bookingService.isSeatBookedForTimeSlot(seatId, bookingDate, start, end);
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("Availability check completed")
                    .data(Map.of(
                            "seatId", seatId,
                            "date", date,
                            "startTime", startTime,
                            "endTime", endTime,
                            "isAvailable", isAvailable
                    ))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error checking seat availability", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error checking availability: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/user/{userId}/dashboard")
    public ResponseEntity<ApiResponse> getUserBookingsForDashboard(
            @PathVariable String userId,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        
        try {
            // Verify user from token if provided
            String tokenUserId = null;
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                try {
                    String token = authHeader.substring(7);
                    tokenUserId = jwtService.extractUserId(token, jwtService.getJwtSecret());
                } catch (Exception e) {
                    log.warn("Failed to extract user from token", e);
                }
            }
            
            // Use token userId if available, otherwise use path userId
            String targetUserId = tokenUserId != null ? tokenUserId : userId;
            
            LocalDate today = LocalDate.now();
            LocalDate tomorrow = today.plusDays(1);
            
            // Get today's bookings
            List<Booking> todayBookings = bookingService.getBookingsByUserAndDateRange(targetUserId, today, today);
            
            // Get upcoming bookings (tomorrow onwards)
            List<Booking> upcomingBookings = bookingService.getBookingsByUserAndDateRange(targetUserId, tomorrow, today.plusMonths(1));
            
            // Get historical bookings (past bookings)
            List<Booking> historicalBookings = bookingService.getBookingsByUserAndDateRange(targetUserId, today.minusMonths(1), today.minusDays(1));
            
            // Convert to responses
            List<BookingResponse> todayResponses = todayBookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            List<BookingResponse> upcomingResponses = upcomingBookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            List<BookingResponse> historicalResponses = historicalBookings.stream()
                    .map(b -> {
                        var userOpt = userRepository.findById(b.getUserId());
                        String userName = userOpt.map(u -> u.getName()).orElse(null);
                        String userEmail = userOpt.map(u -> u.getEmail()).orElse(null);
                        return BookingResponse.fromEntity(b, userName, userEmail);
                    })
                    .toList();
            
            return ResponseEntity.ok(ApiResponse.builder()
                    .success(true)
                    .message("User bookings retrieved successfully")
                    .data(Map.of(
                            "today", Map.of(
                                    "bookings", todayResponses,
                                    "count", todayBookings.size()
                            ),
                            "upcoming", Map.of(
                                    "bookings", upcomingResponses,
                                    "count", upcomingBookings.size()
                            ),
                            "history", Map.of(
                                    "bookings", historicalResponses,
                                    "count", historicalBookings.size()
                            ),
                            "summary", Map.of(
                                    "totalBookings", todayBookings.size() + upcomingBookings.size() + historicalBookings.size(),
                                    "todayCount", todayBookings.size(),
                                    "upcomingCount", upcomingBookings.size(),
                                    "historyCount", historicalBookings.size()
                            )
                    ))
                    .build());
                    
        } catch (Exception e) {
            log.error("Error retrieving user bookings for dashboard", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.builder()
                    .success(false)
                    .message("Error retrieving user bookings: " + e.getMessage())
                    .build());
        }
    }

    private String extractUserId(String authHeader, BookingRequest request) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                String token = authHeader.substring(7);
                return jwtService.extractUserId(token, jwtService.getJwtSecret());
            } catch (Exception e) {
                log.warn("Failed to extract user from token", e);
            }
        }
        
        // Fallback to request userId or generate a default
        return request != null && request.getUserId() != null ? request.getUserId() : "anonymous";
    }
} 