package com.eventplatform.dto;

import lombok.Data;

import java.util.List;

@Data
public class AdminDashboardResponse {

    private Long totalEvents;
    private Long totalUsers;
    private Long totalBookings;
    private Long totalTicketsSold;
    private Double totalRevenue;
    private Long upcomingEvents;
    private Long cancelledEvents;
    private List<BookingResponse> recentBookings;
    private List<EventSummary> topEvents;

    @Data
    public static class EventSummary {
        private Long eventId;
        private String eventTitle;
        private Long ticketsSold;
        private Long attendees;
        private Double revenue;
    }
}
