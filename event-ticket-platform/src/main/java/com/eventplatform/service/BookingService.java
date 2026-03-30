package com.eventplatform.service;

import com.eventplatform.dto.BookingRequest;
import com.eventplatform.dto.BookingResponse;
import com.eventplatform.model.*;
import com.eventplatform.repository.BookingRepository;
import com.eventplatform.repository.PaymentRepository;
import com.eventplatform.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;
    private final PaymentRepository paymentRepository;
    private final EventService eventService;
    private final UserService userService;

    @Transactional
    public BookingResponse bookTickets(BookingRequest request) {
        User currentUser = userService.getCurrentUser();
        Event event = eventService.getEventById(request.getEventId());

        // Validate availability
        if (event.getAvailableTickets() < request.getNumberOfTickets()) {
            throw new RuntimeException("Only " + event.getAvailableTickets() + " tickets available!");
        }
        if (event.getStatus() != Event.EventStatus.UPCOMING && event.getStatus() != Event.EventStatus.ONGOING) {
            throw new RuntimeException("This event is not accepting bookings!");
        }

        // Calculate total amount
        BigDecimal totalAmount = event.getTicketPrice().multiply(BigDecimal.valueOf(request.getNumberOfTickets()));

        // Create booking
        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(currentUser);
        booking.setEvent(event);
        booking.setNumberOfTickets(request.getNumberOfTickets());
        booking.setTotalAmount(totalAmount);
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

        Booking savedBooking = bookingRepository.save(booking);

        // Generate individual tickets
        List<Ticket> tickets = new ArrayList<>();
        for (int i = 1; i <= request.getNumberOfTickets(); i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketId(generateTicketId(event.getId(), savedBooking.getId(), i));
            ticket.setBooking(savedBooking);
            ticket.setUser(currentUser);
            ticket.setEvent(event);
            ticket.setSeatNumber("S" + String.format("%03d", i));
            ticket.setTicketStatus(Ticket.TicketStatus.ACTIVE);
            tickets.add(ticketRepository.save(ticket));
        }

        // Simulate payment
        Payment payment = new Payment();
        payment.setPaymentReference(generatePaymentReference());
        payment.setBooking(savedBooking);
        payment.setUser(currentUser);
        payment.setAmount(totalAmount);
        payment.setPaymentStatus(Payment.PaymentStatus.SUCCESS);
        payment.setTransactionId("TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());

        if (request.getPaymentMethod() != null) {
            try {
                payment.setPaymentMethod(Payment.PaymentMethod.valueOf(request.getPaymentMethod()));
            } catch (IllegalArgumentException e) {
                payment.setPaymentMethod(Payment.PaymentMethod.CREDIT_CARD);
            }
        }

        paymentRepository.save(payment);

        // Decrease available tickets
        eventService.decreaseAvailableTickets(event.getId(), request.getNumberOfTickets());

        return buildBookingResponse(savedBooking, tickets, payment);
    }

    public List<BookingResponse> getUserBookings() {
        User currentUser = userService.getCurrentUser();
        List<Booking> bookings = bookingRepository.findByUserOrderByBookingDateDesc(currentUser);
        return bookings.stream()
                .map(b -> {
                    List<Ticket> tickets = ticketRepository.findByBookingId(b.getId());
                    Payment payment = paymentRepository.findByBookingId(b.getId()).orElse(null);
                    return buildBookingResponse(b, tickets, payment);
                })
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingByReference(String reference) {
        Booking booking = bookingRepository.findByBookingReference(reference)
                .orElseThrow(() -> new RuntimeException("Booking not found: " + reference));
        List<Ticket> tickets = ticketRepository.findByBookingId(booking.getId());
        Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);
        return buildBookingResponse(booking, tickets, payment);
    }

    @Transactional
    public BookingResponse cancelBooking(Long bookingId) {
        User currentUser = userService.getCurrentUser();
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found!"));

        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Unauthorized: Cannot cancel another user's booking");
        }
        if (booking.getBookingStatus() == Booking.BookingStatus.CANCELLED) {
            throw new RuntimeException("Booking is already cancelled!");
        }

        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Cancel tickets
        List<Ticket> tickets = ticketRepository.findByBookingId(bookingId);
        tickets.forEach(t -> {
            t.setTicketStatus(Ticket.TicketStatus.CANCELLED);
            ticketRepository.save(t);
        });

        // Refund payment
        paymentRepository.findByBookingId(bookingId).ifPresent(p -> {
            p.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
            paymentRepository.save(p);
        });

        // Restore available tickets
        eventService.increaseAvailableTickets(booking.getEvent().getId(), booking.getNumberOfTickets());

        return buildBookingResponse(booking, tickets,
                paymentRepository.findByBookingId(bookingId).orElse(null));
    }

    public List<BookingResponse> getAllBookings() {
        return bookingRepository.findAllWithDetails().stream()
                .map(b -> {
                    List<Ticket> tickets = ticketRepository.findByBookingId(b.getId());
                    Payment payment = paymentRepository.findByBookingId(b.getId()).orElse(null);
                    return buildBookingResponse(b, tickets, payment);
                })
                .collect(Collectors.toList());
    }

    public Long getTotalTicketsSold() {
        return bookingRepository.countTotalTicketsSold();
    }

    public Double getTotalRevenue() {
        return bookingRepository.calculateTotalRevenue();
    }

    public Long getTotalBookings() {
        return bookingRepository.count();
    }

    private BookingResponse buildBookingResponse(Booking booking, List<Ticket> tickets, Payment payment) {
        BookingResponse response = new BookingResponse();
        response.setBookingId(booking.getId());
        response.setBookingReference(booking.getBookingReference());
        response.setEventTitle(booking.getEvent().getTitle());
        response.setEventVenue(booking.getEvent().getVenue());
        response.setEventLocation(booking.getEvent().getLocation());
        response.setEventDate(booking.getEvent().getEventDate());
        response.setNumberOfTickets(booking.getNumberOfTickets());
        response.setTotalAmount(booking.getTotalAmount());
        response.setBookingStatus(booking.getBookingStatus().name());
        response.setBookingDate(booking.getBookingDate());
        response.setUsername(booking.getUser().getUsername());
        response.setUserFullName(booking.getUser().getFullName());
        response.setUserEmail(booking.getUser().getEmail());

        if (payment != null) {
            response.setPaymentReference(payment.getPaymentReference());
            response.setPaymentStatus(payment.getPaymentStatus().name());
        }

        if (tickets != null) {
            response.setTicketIds(tickets.stream().map(Ticket::getTicketId).collect(Collectors.toList()));
        }

        return response;
    }

    private String generateBookingReference() {
        return "BK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String generateTicketId(Long eventId, Long bookingId, int index) {
        return "TKT-" + eventId + "-" + bookingId + "-" + String.format("%03d", index);
    }

    private String generatePaymentReference() {
        return "PAY-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
    }
}
