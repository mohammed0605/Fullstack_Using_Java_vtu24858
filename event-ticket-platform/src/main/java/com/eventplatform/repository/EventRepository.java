package com.eventplatform.repository;

import com.eventplatform.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByStatusOrderByEventDateAsc(Event.EventStatus status);

    List<Event> findByCategoryOrderByEventDateAsc(String category);

    List<Event> findByTitleContainingIgnoreCase(String keyword);

    @Query("SELECT e FROM Event e WHERE e.availableTickets > 0 AND e.status = 'UPCOMING' ORDER BY e.eventDate ASC")
    List<Event> findAvailableEvents();

    @Query("SELECT COUNT(e) FROM Event e")
    Long countAllEvents();

    @Query("SELECT COUNT(e) FROM Event e WHERE e.status = :status")
    Long countByStatus(Event.EventStatus status);
}
