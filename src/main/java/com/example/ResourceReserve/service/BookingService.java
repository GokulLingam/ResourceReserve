package com.example.ResourceReserve.service;

import com.example.ResourceReserve.dto.BookingRequest;
import com.example.ResourceReserve.entity.Booking;
import com.example.ResourceReserve.entity.Booking.BookType;
import com.example.ResourceReserve.entity.Booking.RecurrenceType;
import com.example.ResourceReserve.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingService {

    private final BookingRepository bookingRepository;

    public List<Booking> createBooking(BookingRequest request, String userId) {
        log.info("Creating booking for user: {}, seat: {}, date: {}", userId, request.getSubType(), request.getDate());
        
        try {
            log.info("Generating booking dates...");
            List<LocalDate> bookingDates = generateBookingDates(request);
            log.info("Generated {} booking dates", bookingDates.size());
            
            List<Booking> savedBookings = new ArrayList<>();
            
            log.info("Parsing time values...");
            LocalTime startTime = LocalTime.parse(request.getStartTime());
            LocalTime endTime = LocalTime.parse(request.getEndTime());
            log.info("Parsed times: {} to {}", startTime, endTime);
            
            // Validate time
            if (endTime.isBefore(startTime)) {
                throw new IllegalArgumentException("End time cannot be before start time");
            }
            
            for (LocalDate date : bookingDates) {
                log.info("Processing date: {}", date);
                
                // Check for booking conflicts
                log.info("Checking for booking conflicts...");
                if (hasBookingConflict(date, startTime, endTime, request.getBookType(), request.getSubType())) {
                    throw new IllegalStateException("Item already booked on " + date);
                }
                log.info("No conflicts found");
                
                // Create booking
                log.info("Creating booking entity...");
                Booking booking = createBookingEntity(request, date, startTime, endTime, userId);
                log.info("Saving booking to database...");
                savedBookings.add(bookingRepository.save(booking));
                log.info("Booking saved successfully");
            }
            
            log.info("All bookings created successfully, total: {}", savedBookings.size());
            return savedBookings;
        } catch (Exception e) {
            log.error("Error in createBooking: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public List<Booking> getBookings(String userId, String seatId, String date, String status) {
        if (userId != null) {
            return bookingRepository.findByUserIdOrderByDateDesc(userId);
        } else if (seatId != null) {
            return bookingRepository.findBySubTypeOrderByDateDesc(seatId);
        } else if (date != null) {
            LocalDate bookingDate = LocalDate.parse(date);
            return bookingRepository.findByDateBetweenOrderByDate(bookingDate, bookingDate);
        } else if (status != null) {
            return bookingRepository.findByStatusOrderByDateDesc(status);
        } else {
            return bookingRepository.findAll();
        }
    }
    
    public Optional<Booking> getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId);
    }
    
    public Booking cancelBooking(String bookingId, String userId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new IllegalArgumentException("Booking not found");
        }
        
        Booking booking = bookingOpt.get();
        
        // Check if user is authorized to cancel this booking
        if (!booking.getUserId().equals(userId)) {
            throw new IllegalStateException("Not authorized to cancel this booking");
        }
        
        booking.setStatus("cancelled");
        return bookingRepository.save(booking);
    }
    
    public boolean hasBookingConflict(LocalDate date, LocalTime startTime, LocalTime endTime, 
                                    BookType bookType, String subType) {
        log.debug("Checking booking conflict for date: {}, startTime: {}, endTime: {}, bookType: {}, subType: {}", 
                 date, startTime, endTime, bookType, subType);
        
        // Simple approach: just check if there's any booking on the same date and seat
        int bookingCount = bookingRepository.countBookingsForDateAndSeat(date, bookType.name(), subType);
        boolean hasConflict = bookingCount > 0;
        log.debug("Booking count: {}, has conflict: {}", bookingCount, hasConflict);
        return hasConflict;
    }
    
    public List<Booking> getBookingsByLocation(String officeLocation, String building, String floor, LocalDate date) {
        return bookingRepository.findByOfficeLocationAndBuildingAndFloorAndDate(
                officeLocation, building, floor, date);
    }
    
    public List<Booking> getBookingsByDateRange(LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByDateBetweenOrderByDate(startDate, endDate);
    }
    
    public List<Booking> getBookingsByUserAndDateRange(String userId, LocalDate startDate, LocalDate endDate) {
        return bookingRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, startDate, endDate);
    }
    
    public List<Booking> getActiveBookingsBySeatAndDate(String seatId, LocalDate date) {
        return bookingRepository.findActiveBookingsBySeatAndDate(seatId, date);
    }
    
    public boolean isSeatBookedForTimeSlot(String seatId, LocalDate date, LocalTime startTime, LocalTime endTime) {
        int bookingCount = bookingRepository.countSeatBookingsForTimeSlot(seatId, date, startTime, endTime);
        return bookingCount > 0;
    }
    
    private List<LocalDate> generateBookingDates(BookingRequest request) {
        List<LocalDate> bookingDates = new ArrayList<>();
        LocalDate startDate = LocalDate.parse(request.getDate());
        
        // Validate recurrence for DAILY and WEEKLY
        if (request.getRecurrence() != null &&
                (request.getRecurrence().getType() == RecurrenceType.DAILY || 
                 request.getRecurrence().getType() == RecurrenceType.WEEKLY)) {

            if (request.getRecurrence().getEndDate() == null) {
                throw new IllegalArgumentException("endDate is required for recurrence type " + request.getRecurrence().getType());
            }

            LocalDate end = LocalDate.parse(request.getRecurrence().getEndDate());
            if (end.isBefore(startDate)) {
                throw new IllegalArgumentException("endDate must be after start date");
            }
        }
        
        // Generate booking dates based on recurrence
        if (request.getRecurrence() == null || request.getRecurrence().getType() == RecurrenceType.NONE) {
            bookingDates.add(startDate);
        } else {
            switch (request.getRecurrence().getType()) {
                case DAILY -> {
                    LocalDate end = LocalDate.parse(request.getRecurrence().getEndDate());
                    for (LocalDate d = startDate; !d.isAfter(end); d = d.plusDays(1)) {
                        bookingDates.add(d);
                    }
                }
                case WEEKLY -> {
                    LocalDate end = LocalDate.parse(request.getRecurrence().getEndDate());
                    for (LocalDate d = startDate; !d.isAfter(end); d = d.plusWeeks(1)) {
                        bookingDates.add(d);
                    }
                }
                case CUSTOM -> {
                    if (request.getRecurrence().getCustomDates() == null || request.getRecurrence().getCustomDates().isEmpty()) {
                        throw new IllegalArgumentException("Custom dates required for recurrence type CUSTOM");
                    }
                    for (String dateStr : request.getRecurrence().getCustomDates()) {
                        bookingDates.add(LocalDate.parse(dateStr));
                    }
                }
            }
        }
        
        return bookingDates;
    }
    
    private Booking createBookingEntity(BookingRequest request, LocalDate date, LocalTime startTime, 
                                      LocalTime endTime, String userId) {
        String bookingId = request.getId() != null ? request.getId() + "_" + date : UUID.randomUUID().toString() + "_" + date;
        
        Booking booking = new Booking(
                bookingId,
                date,
                startTime,
                endTime,
                request.getBookType(),
                request.getSubType(),
                request.getOfficeLocation(),
                request.getBuilding(),
                request.getFloor(),
                request.getRecurrence() != null ? request.getRecurrence().getType() : RecurrenceType.NONE,
                request.getRecurrence() != null && request.getRecurrence().getEndDate() != null
                        ? LocalDate.parse(request.getRecurrence().getEndDate()) : null,
                request.getRecurrence() != null && request.getRecurrence().getCustomDates() != null
                        ? request.getRecurrence().getCustomDates().stream().map(LocalDate::parse).toList()
                        : null,
                "confirmed"
        );
        
        booking.setUserId(userId);
        booking.setNotes(request.getNotes());
        
        return booking;
    }
} 