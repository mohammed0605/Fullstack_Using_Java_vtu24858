package com.eventplatform.service;

import com.eventplatform.dto.EventRequest;
import com.eventplatform.model.Event;
import com.eventplatform.model.User;
import com.eventplatform.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public Event createEvent(EventRequest request) {
        User currentUser = userService.getCurrentUser();

        Event event = new Event();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setVenue(request.getVenue());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setTotalTickets(request.getTotalTickets());
        event.setAvailableTickets(request.getTotalTickets());
        event.setTicketPrice(request.getTicketPrice());
        event.setCategory(request.getCategory());
        event.setImageUrl(request.getImageUrl());
        event.setStatus(Event.EventStatus.UPCOMING);
        event.setCreatedBy(currentUser);

        return eventRepository.save(event);
    }

    @Transactional
    public Event updateEvent(Long id, EventRequest request) {
        Event event = getEventById(id);

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setVenue(request.getVenue());
        event.setLocation(request.getLocation());
        event.setEventDate(request.getEventDate());
        event.setTicketPrice(request.getTicketPrice());
        event.setCategory(request.getCategory());
        event.setImageUrl(request.getImageUrl());

        if (request.getStatus() != null) {
            try {
                event.setStatus(Event.EventStatus.valueOf(request.getStatus()));
            } catch (IllegalArgumentException ignored) {}
        }

        // Update total tickets only if increasing
        if (request.getTotalTickets() != null && request.getTotalTickets() > event.getTotalTickets()) {
            int additionalTickets = request.getTotalTickets() - event.getTotalTickets();
            event.setTotalTickets(request.getTotalTickets());
            event.setAvailableTickets(event.getAvailableTickets() + additionalTickets);
        }

        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        Event event = getEventById(id);
        eventRepository.delete(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found with id: " + id));
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getAvailableEvents() {
        return eventRepository.findAvailableEvents();
    }

    public List<Event> getEventsByStatus(String status) {
        try {
            return eventRepository.findByStatusOrderByEventDateAsc(Event.EventStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            return getAllEvents();
        }
    }

    public List<Event> searchEvents(String keyword) {
        return eventRepository.findByTitleContainingIgnoreCase(keyword);
    }

    public Long countAllEvents() {
        return eventRepository.countAllEvents();
    }

    public Long countByStatus(String status) {
        try {
            return eventRepository.countByStatus(Event.EventStatus.valueOf(status));
        } catch (IllegalArgumentException e) {
            return 0L;
        }
    }

    @Transactional
    public void decreaseAvailableTickets(Long eventId, int count) {
        Event event = getEventById(eventId);
        if (event.getAvailableTickets() < count) {
            throw new RuntimeException("Not enough tickets available!");
        }
        event.setAvailableTickets(event.getAvailableTickets() - count);
        eventRepository.save(event);
    }

    @Transactional
    public void increaseAvailableTickets(Long eventId, int count) {
        Event event = getEventById(eventId);
        event.setAvailableTickets(event.getAvailableTickets() + count);
        eventRepository.save(event);
    }
}
