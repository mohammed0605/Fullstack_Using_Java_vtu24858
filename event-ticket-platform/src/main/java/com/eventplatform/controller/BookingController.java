package com.eventplatform.controller;

import com.eventplatform.dto.BookingRequest;
import com.eventplatform.dto.BookingResponse;
import com.eventplatform.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> bookTickets(@Valid @RequestBody BookingRequest request) {
        try {
            BookingResponse response = bookingService.bookTickets(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/my-tickets")
    public ResponseEntity<List<BookingResponse>> getMyTickets() {
        return ResponseEntity.ok(bookingService.getUserBookings());
    }

    @GetMapping("/{reference}")
    public ResponseEntity<?> getBookingByReference(@PathVariable String reference) {
        try {
            BookingResponse response = bookingService.getBookingByReference(reference);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable Long id) {
        try {
            BookingResponse response = bookingService.cancelBooking(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
