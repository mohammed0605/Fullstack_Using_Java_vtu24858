package com.eventplatform.repository;

import com.eventplatform.model.Booking;
import com.eventplatform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByBookingDateDesc(User user);

    List<Booking> findByEventIdOrderByBookingDateDesc(Long eventId);

    Optional<Booking> findByBookingReference(String bookingReference);

    @Query("SELECT COALESCE(SUM(b.numberOfTickets), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED'")
    Long countTotalTicketsSold();

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.bookingStatus = 'CONFIRMED'")
    Double calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.numberOfTickets), 0) FROM Booking b WHERE b.event.id = :eventId AND b.bookingStatus = 'CONFIRMED'")
    Long countTicketsSoldForEvent(@Param("eventId") Long eventId);

    @Query("SELECT b FROM Booking b JOIN FETCH b.event JOIN FETCH b.user ORDER BY b.bookingDate DESC")
    List<Booking> findAllWithDetails();

    @Query("SELECT COUNT(DISTINCT b.user) FROM Booking b WHERE b.event.id = :eventId AND b.bookingStatus = 'CONFIRMED'")
    Long countAttendeesForEvent(@Param("eventId") Long eventId);
}
