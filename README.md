# Smart Campus Operations Hub

A full-stack web application for managing campus facilities, bookings, maintenance tickets, and notifications. Built with **Spring Boot** (backend) and **React** (frontend).

## 🏗️ Architecture

```
PAF_project/
├── src/main/java/com/smartcampus/    # Spring Boot Backend
│   ├── config/                        # Security, CORS, MongoDB config
│   ├── controller/                    # REST API endpoints
│   ├── dto/                           # Request/Response DTOs
│   ├── exception/                     # Custom exceptions & global handler
│   ├── model/                         # MongoDB document entities
│   ├── repository/                    # MongoDB repositories
│   ├── security/                      # JWT, OAuth2, filters
│   └── service/                       # Business logic
├── frontend/                          # React SPA (Vite + Tailwind)
│   └── src/
│       ├── components/                # Reusable UI components
│       ├── context/                   # Auth context
│       ├── pages/                     # Page components
│       └── services/                  # API service layer
└── .github/workflows/                 # CI pipeline
```

## 📋 Modules

### Module A: Facilities & Assets Catalogue
- CRUD operations for campus resources (Lecture Halls, Labs, Meeting Rooms, etc.)
- Search/filter by type, status, capacity, location
- Resource statuses: ACTIVE, UNDER_MAINTENANCE, OUT_OF_SERVICE

### Module B: Booking Management
- Book campus resources with date/time selection
- Booking statuses: PENDING → APPROVED/REJECTED
- Conflict detection for overlapping bookings
- Users can cancel pending bookings

### Module C: Maintenance & Incident Ticketing
- Report issues with up to 3 image attachments
- Ticket lifecycle: OPEN → IN_PROGRESS → RESOLVED → CLOSED
- Technician assignment by admins
- Comment system with ownership-based edit/delete

### Module D: Notifications
- Automatic notifications for booking approvals/rejections
- Ticket status change notifications
- New comment notifications
- Web UI notification panel with unread indicators

### Module E: Authentication & Authorization
- Local registration/login with JWT tokens
- Google OAuth 2.0 sign-in
- Role-based access: USER, ADMIN, TECHNICIAN
- Protected API endpoints with role checks

## 🚀 Tech Stack

| Layer     | Technology                         |
|-----------|-------------------------------------|
| Backend   | Spring Boot 3.2.4, Java 17         |
| Database  | MongoDB                             |
| Security  | Spring Security, JWT, OAuth2        |
| Frontend  | React 18, Vite 5, Tailwind CSS 3   |
| CI/CD     | GitHub Actions                      |

## ⚙️ Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- MongoDB 6+ (running on localhost:27017)

## 🏃 Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd PAF_project
```

### 2. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/smart_campus_db

# JWT Configuration
jwt.secret=your-secret-key-minimum-32-characters
jwt.expiration=86400000

# Google OAuth2 (optional)
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

### 3. Start the Backend
```bash
mvn spring-boot:run
```
Backend runs on http://localhost:8080

### 4. Start the Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on http://localhost:5173

## 📡 API Endpoints

### Auth (`/api/auth`)
| Method | Endpoint       | Description          | Auth |
|--------|---------------|----------------------|------|
| POST   | /register     | Register new user    | No   |
| POST   | /login        | Login & get JWT      | No   |
| GET    | /me           | Get current user     | Yes  |

### Resources (`/api/resources`)
| Method | Endpoint       | Description          | Auth  |
|--------|---------------|----------------------|-------|
| GET    | /             | List all resources   | Yes   |
| GET    | /{id}         | Get resource by ID   | Yes   |
| GET    | /search       | Search/filter        | Yes   |
| POST   | /             | Create resource      | ADMIN |
| PUT    | /{id}         | Update resource      | ADMIN |
| DELETE | /{id}         | Delete resource      | ADMIN |

### Bookings (`/api/bookings`)
| Method | Endpoint              | Description           | Auth  |
|--------|-----------------------|-----------------------|-------|
| POST   | /                     | Create booking        | Yes   |
| GET    | /my                   | My bookings           | Yes   |
| GET    | /{id}                 | Get booking by ID     | Yes   |
| GET    | /                     | All bookings          | ADMIN |
| PUT    | /{id}/review          | Approve/Reject        | ADMIN |
| PATCH  | /{id}/cancel          | Cancel booking        | Yes   |
| GET    | /resource/{resourceId}| Bookings for resource | Yes   |

### Tickets (`/api/tickets`)
| Method | Endpoint                    | Description            | Auth      |
|--------|-----------------------------|------------------------|-----------|
| POST   | /                           | Create ticket (multipart)| Yes     |
| GET    | /my                         | My tickets             | Yes       |
| GET    | /{id}                       | Get ticket by ID       | Yes       |
| GET    | /                           | All tickets            | ADMIN     |
| PUT    | /{id}/status                | Update status          | ADMIN/TECH|
| PATCH  | /{id}/assign/{techId}       | Assign technician      | ADMIN     |
| GET    | /assigned                   | Assigned tickets       | TECH      |
| DELETE | /{id}                       | Delete ticket          | Yes       |

### Comments (`/api/tickets/{ticketId}/comments`)
| Method | Endpoint       | Description          | Auth |
|--------|---------------|----------------------|------|
| POST   | /             | Add comment          | Yes  |
| GET    | /             | Get ticket comments  | Yes  |
| PUT    | /{commentId}  | Update comment       | Yes  |
| DELETE | /{commentId}  | Delete comment       | Yes  |

### Notifications (`/api/notifications`)
| Method | Endpoint       | Description          | Auth |
|--------|---------------|----------------------|------|
| GET    | /             | All notifications    | Yes  |
| GET    | /unread       | Unread notifications | Yes  |
| GET    | /unread/count | Unread count         | Yes  |
| PATCH  | /{id}/read    | Mark as read         | Yes  |
| PATCH  | /read-all     | Mark all as read     | Yes  |
| DELETE | /{id}         | Delete notification  | Yes  |

### Admin (`/api/admin`)
| Method | Endpoint              | Description          | Auth  |
|--------|-----------------------|----------------------|-------|
| GET    | /users                | List all users       | ADMIN |
| GET    | /users/role/{role}    | Users by role        | ADMIN |
| PUT    | /users/{userId}/roles | Update user roles    | ADMIN |

## 🧪 Testing

```bash
# Run backend tests
mvn test

# Run frontend build check
cd frontend && npm run build
```

## 👥 Team Members

| Member | IT Number | Module(s) Implemented |
|--------|-----------|----------------------|
|        |           |                      |
|        |           |                      |
|        |           |                      |
|        |           |                      |

## 📄 License

This project is created as part of the IT3030 - PAF module assignment at SLIIT.
