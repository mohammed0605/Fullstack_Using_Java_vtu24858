-- ============================================
-- Event Ticket Platform - Database Schema
-- ============================================

CREATE DATABASE IF NOT EXISTS event_platform;
USE event_platform;

-- ============================================
-- USERS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    role ENUM('USER', 'ADMIN') DEFAULT 'USER',
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- ============================================
-- EVENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    venue VARCHAR(200) NOT NULL,
    location VARCHAR(200) NOT NULL,
    event_date DATETIME NOT NULL,
    total_tickets INT NOT NULL DEFAULT 0,
    available_tickets INT NOT NULL DEFAULT 0,
    ticket_price DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    category VARCHAR(100),
    image_url VARCHAR(500),
    status ENUM('UPCOMING', 'ONGOING', 'COMPLETED', 'CANCELLED') DEFAULT 'UPCOMING',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- ============================================
-- BOOKINGS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    booking_reference VARCHAR(20) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    number_of_tickets INT NOT NULL DEFAULT 1,
    total_amount DECIMAL(10, 2) NOT NULL,
    booking_status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') DEFAULT 'PENDING',
    booking_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- ============================================
-- TICKETS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS tickets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ticket_id VARCHAR(30) NOT NULL UNIQUE,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    event_id BIGINT NOT NULL,
    seat_number VARCHAR(20),
    ticket_status ENUM('ACTIVE', 'USED', 'CANCELLED') DEFAULT 'ACTIVE',
    issued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- ============================================
-- PAYMENTS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_reference VARCHAR(30) NOT NULL UNIQUE,
    booking_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CREDIT_CARD', 'DEBIT_CARD', 'NET_BANKING', 'UPI', 'WALLET') DEFAULT 'CREDIT_CARD',
    payment_status ENUM('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED') DEFAULT 'PENDING',
    transaction_id VARCHAR(100),
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (booking_id) REFERENCES bookings(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- ============================================
-- INDEXES
-- ============================================
CREATE INDEX idx_events_status ON events(status);
CREATE INDEX idx_events_date ON events(event_date);
CREATE INDEX idx_bookings_user ON bookings(user_id);
CREATE INDEX idx_bookings_event ON bookings(event_id);
CREATE INDEX idx_tickets_booking ON tickets(booking_id);
CREATE INDEX idx_payments_booking ON payments(booking_id);

-- ============================================
-- DEFAULT ADMIN USER
-- Password: admin123 (BCrypt encoded)
-- ============================================
INSERT IGNORE INTO users (username, email, password, full_name, role) VALUES
('admin', 'admin@eventplatform.com', '₹2a₹10₹N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Platform Administrator', 'ADMIN');

-- ============================================
-- SAMPLE EVENTS
-- ============================================
INSERT IGNORE INTO events (title, description, venue, location, event_date, total_tickets, available_tickets, ticket_price, category, status, created_by) VALUES
('Tech Summit 2025', 'Annual technology conference featuring the latest innovations in AI, Cloud, and DevOps.', 'Convention Center Hall A', 'New York, NY', '2025-08-15 09:00:00', 500, 500, 49.99, 'Technology', 'UPCOMING', 1),
('Music Fest Live', 'An unforgettable outdoor music festival with top artists from around the world.', 'Central Park Amphitheater', 'New York, NY', '2025-09-20 18:00:00', 1000, 1000, 79.99, 'Music', 'UPCOMING', 1),
('Business Leadership Workshop', 'Intensive workshop for aspiring entrepreneurs and business leaders.', 'Grand Hyatt Ballroom', 'Chicago, IL', '2025-07-10 10:00:00', 200, 200, 129.99, 'Business', 'UPCOMING', 1),
('Art & Culture Exhibition', 'Explore contemporary art from emerging artists and renowned creators.', 'City Art Museum', 'Los Angeles, CA', '2025-08-05 11:00:00', 300, 300, 25.00, 'Art', 'UPCOMING', 1),
('Comedy Night Special', 'A hilarious evening with top stand-up comedians.', 'Laugh Factory', 'San Francisco, CA', '2025-07-25 20:00:00', 150, 150, 35.00, 'Entertainment', 'UPCOMING', 1);
