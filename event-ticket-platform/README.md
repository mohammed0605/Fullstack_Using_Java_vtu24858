# 🎟️ Event Ticket Platform

A full-stack Event Lifecycle & Ticketing Platform built with Java Spring Boot, MySQL, and vanilla JavaScript.

---

## 🚀 Features

| Feature | Description |
|---|---|
| **User Auth** | Register, Login with JWT + BCrypt encryption |
| **Event Management** | Admin CRUD for events |
| **Event Listing** | Browse, search and filter events |
| **Ticket Booking** | Select tickets, check availability, confirm |
| **Payment Simulation** | Simulated payment processing |
| **Ticket Generation** | Unique ticket IDs per booking |
| **User Dashboard** | View bookings, cancel tickets |
| **Admin Dashboard** | Stats, revenue, attendee list |

---

## 🛠 Tech Stack

- **Backend:** Java 17, Spring Boot 3.2, Spring Security, JPA/Hibernate
- **Frontend:** HTML5, CSS3, Vanilla JavaScript
- **Database:** MySQL 8+
- **Auth:** JWT (JSON Web Tokens)
- **Build:** Maven

---

## ⚙️ Prerequisites

- Java 17+
- MySQL 8+
- Maven 3.8+

---

## 🔧 Setup & Run

### 1. Clone / Download the project

```bash
cd event-ticket-platform
```

### 2. Setup MySQL Database

```bash
mysql -u root -p < database/schema.sql
```

Or connect to MySQL and run the schema manually.

### 3. Configure Database Connection

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/event_platform?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

### 5. Access the Application

Open your browser: **http://localhost:8080**

---

## 🔑 Default Credentials

| Role | Username | Password |
|---|---|---|
| Admin | `admin` | `admin123` |
| User | Register a new account | - |

---

## 📁 Project Structure

```
event-ticket-platform/
├── src/main/java/com/eventplatform/
│   ├── controller/
│   │   ├── UserController.java        # Auth endpoints
│   │   ├── EventController.java       # Event CRUD endpoints
│   │   ├── BookingController.java     # Booking endpoints
│   │   ├── AdminController.java       # Admin dashboard endpoints
│   │   └── WebController.java        # HTML page routing
│   ├── service/
│   │   ├── UserService.java
│   │   ├── EventService.java
│   │   └── BookingService.java
│   ├── repository/
│   │   ├── UserRepository.java
│   │   ├── EventRepository.java
│   │   ├── BookingRepository.java
│   │   ├── TicketRepository.java
│   │   └── PaymentRepository.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Event.java
│   │   ├── Booking.java
│   │   ├── Ticket.java
│   │   └── Payment.java
│   ├── dto/
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── AuthResponse.java
│   │   ├── EventRequest.java
│   │   ├── BookingRequest.java
│   │   ├── BookingResponse.java
│   │   └── AdminDashboardResponse.java
│   ├── security/
│   │   ├── JwtUtils.java
│   │   ├── AuthTokenFilter.java
│   │   └── UserDetailsServiceImpl.java
│   ├── config/
│   │   └── SecurityConfig.java
│   └── EventTicketPlatformApplication.java
├── src/main/resources/
│   ├── static/
│   │   ├── css/style.css
│   │   ├── js/script.js
│   │   ├── login.html
│   │   ├── register.html
│   │   ├── events.html
│   │   ├── book-ticket.html
│   │   ├── dashboard.html
│   │   └── admin.html
│   └── application.properties
├── database/
│   └── schema.sql
├── pom.xml
└── README.md
```

---

## 📡 REST API Endpoints

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login |
| GET | `/api/auth/me` | Get current user info |

### Events
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/events` | Public | List available events |
| GET | `/api/events/all` | Admin | List all events |
| GET | `/api/events/{id}` | Public | Get event details |
| POST | `/api/events` | Admin | Create event |
| PUT | `/api/events/{id}` | Admin | Update event |
| DELETE | `/api/events/{id}` | Admin | Delete event |

### Bookings
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/bookings` | User | Book tickets |
| GET | `/api/bookings/my-tickets` | User | Get my bookings |
| GET | `/api/bookings/{reference}` | User | Get booking by reference |
| PUT | `/api/bookings/{id}/cancel` | User | Cancel booking |

### Admin
| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/admin/dashboard` | Admin | Dashboard stats |
| GET | `/api/admin/bookings` | Admin | All bookings |
| GET | `/api/admin/users` | Admin | All users |
| GET | `/api/admin/events/{id}/attendees` | Admin | Event attendees |

---

## 🗃️ Database Schema

- **users** - User accounts with roles
- **events** - Event listings
- **bookings** - Ticket bookings
- **tickets** - Individual ticket records
- **payments** - Payment records (simulated)

---

## 🧪 Testing the Platform

1. **As a user:** Register → Browse Events → Book Tickets → View Dashboard
2. **As admin:** Login with admin/admin123 → Admin Dashboard → Create/Edit Events → View bookings/users

---

## 📝 Notes

- Passwords are encrypted with BCrypt
- JWT tokens expire after 24 hours
- Payment is fully simulated (no real transactions)
- Booking cancellations automatically restore available ticket count
