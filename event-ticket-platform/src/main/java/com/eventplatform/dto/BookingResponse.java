package com.eventplatform.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingResponse {

    private Long bookingId;
    private String bookingReference;
    private String eventTitle;
    private String eventVenue;
    private String eventLocation;
    private LocalDateTime eventDate;
    private Integer numberOfTickets;
    private BigDecimal totalAmount;
    private String bookingStatus;
    private LocalDateTime bookingDate;
    private String paymentReference;
    private String paymentStatus;
    private List<String> ticketIds;
    private String username;
    private String userFullName;
    private String userEmail;
}
