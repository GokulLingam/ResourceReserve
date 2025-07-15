package com.example.ResourceReserve.repository;

import com.example.ResourceReserve.entity.Booking;
import com.example.ResourceReserve.entity.Booking.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    // Check for booking conflicts - simplified approach
    @Query(value = "SELECT COUNT(*) FROM bookings b " +
           "WHERE b.date = :date " +
           "AND b.book_type = :bookType AND b.sub_type = :subType " +
           "AND b.status = 'confirmed' " +
           "AND NOT (b.end_time <= :startTime OR b.start_time >= :endTime)", 
           nativeQuery = true)
    int countConflictingBookings(@Param("date") LocalDate date, 
                                @Param("startTime") LocalTime startTime, 
                                @Param("endTime") LocalTime endTime, 
                                @Param("bookType") String bookType, 
                                @Param("subType") String subType);
    
    // Fallback method - check for any booking on the same date and seat
    @Query(value = "SELECT COUNT(*) FROM bookings b " +
           "WHERE b.date = :date " +
           "AND b.book_type = :bookType AND b.sub_type = :subType " +
           "AND b.status = 'confirmed'", 
           nativeQuery = true)
    int countBookingsForDateAndSeat(@Param("date") LocalDate date, 
                                   @Param("bookType") String bookType, 
                                   @Param("subType") String subType);
    
    // Find bookings by user
    List<Booking> findByUserIdOrderByDateDesc(String userId);
    
    // Find bookings by seat (subType)
    List<Booking> findBySubTypeOrderByDateDesc(String subType);
    
    // Find bookings by date range
    List<Booking> findByDateBetweenOrderByDate(LocalDate startDate, LocalDate endDate);
    
    // Find bookings by location
    List<Booking> findByOfficeLocationAndBuildingAndFloorAndDate(
            String officeLocation, String building, String floor, LocalDate date);
    
    // Find bookings by status
    List<Booking> findByStatusOrderByDateDesc(String status);
    
    // Find active bookings for a specific seat on a specific date
    @Query(value = "SELECT * FROM bookings b WHERE b.sub_type = :seatId AND b.date = :date AND b.status = 'confirmed'", 
           nativeQuery = true)
    List<Booking> findActiveBookingsBySeatAndDate(@Param("seatId") String seatId, @Param("date") LocalDate date);
    
    // Find all bookings for a specific seat
    List<Booking> findBySubTypeAndStatusOrderByDateDesc(String subType, String status);
    
    // Find bookings by user and date range
    List<Booking> findByUserIdAndDateBetweenOrderByDateDesc(String userId, LocalDate startDate, LocalDate endDate);
    
    // Find bookings by location and date range
    List<Booking> findByOfficeLocationAndBuildingAndFloorAndDateBetweenOrderByDateDesc(
            String officeLocation, String building, String floor, LocalDate startDate, LocalDate endDate);
    
    // Check if seat is booked for a specific time slot
    @Query(value = "SELECT COUNT(*) FROM bookings b " +
           "WHERE b.sub_type = :seatId AND b.date = :date " +
           "AND b.status = 'confirmed' AND " +
           "NOT (b.end_time <= :startTime OR b.start_time >= :endTime)", 
           nativeQuery = true)
    int countSeatBookingsForTimeSlot(@Param("seatId") String seatId, 
                                    @Param("date") LocalDate date, 
                                    @Param("startTime") LocalTime startTime, 
                                    @Param("endTime") LocalTime endTime);
} 