package com.eventplatform.controller;

import com.eventplatform.dto.AdminDashboardResponse;
import com.eventplatform.dto.BookingResponse;
import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.repository.BookingRepository;
import com.eventplatform.service.BookingService;
import com.eventplatform.service.EventService;
import com.eventplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdminController {

    private final BookingService bookingService;
    private final EventService eventService;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<AdminDashboardResponse> getDashboard() {
        AdminDashboardResponse dashboard = new AdminDashboardResponse();

        dashboard.setTotalEvents(eventService.countAllEvents());
        dashboard.setTotalUsers(userService.getTotalUsers());
        dashboard.setTotalBookings(bookingService.getTotalBookings());
        dashboard.setTotalTicketsSold(bookingService.getTotalTicketsSold());
        dashboard.setTotalRevenue(bookingService.getTotalRevenue());
        dashboard.setUpcomingEvents(eventService.countByStatus("UPCOMING"));
        dashboard.setCancelledEvents(eventService.countByStatus("CANCELLED"));

        // Recent bookings (last 10)
        List<BookingResponse> allBookings = bookingService.getAllBookings();
        dashboard.setRecentBookings(allBookings.stream().limit(10).collect(Collectors.toList()));

        // Top events summary
        List<Event> allEvents = eventService.getAllEvents();
        List<AdminDashboardResponse.EventSummary> topEvents = allEvents.stream()
                .map(e -> {
                    AdminDashboardResponse.EventSummary summary = new AdminDashboardResponse.EventSummary();
                    summary.setEventId(e.getId());
                    summary.setEventTitle(e.getTitle());
                    Long sold = bookingRepository.countTicketsSoldForEvent(e.getId());
                    summary.setTicketsSold(sold);
                    Long attendees = bookingRepository.countAttendeesForEvent(e.getId());
                    summary.setAttendees(attendees);
                    summary.setRevenue(sold * e.getTicketPrice().doubleValue());
                    return summary;
                })
                .collect(Collectors.toList());

        dashboard.setTopEvents(topEvents);

        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/events/{eventId}/attendees")
    public ResponseEntity<List<BookingResponse>> getEventAttendees(@PathVariable Long eventId) {
        List<BookingResponse> attendees = bookingRepository.findByEventIdOrderByBookingDateDesc(eventId)
                .stream()
                .filter(b -> b.getBookingStatus() == com.eventplatform.model.Booking.BookingStatus.CONFIRMED)
                .map(b -> {
                    BookingResponse r = new BookingResponse();
                    r.setBookingId(b.getId());
                    r.setBookingReference(b.getBookingReference());
                    r.setUsername(b.getUser().getUsername());
                    r.setUserFullName(b.getUser().getFullName());
                    r.setUserEmail(b.getUser().getEmail());
                    r.setNumberOfTickets(b.getNumberOfTickets());
                    r.setTotalAmount(b.getTotalAmount());
                    r.setBookingStatus(b.getBookingStatus().name());
                    r.setBookingDate(b.getBookingDate());
                    return r;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(attendees);
    }
}
