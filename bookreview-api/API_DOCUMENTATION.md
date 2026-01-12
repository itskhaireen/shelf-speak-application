# ShelfSpeak API Documentation

*---because books finally speak™*

## Overview
ShelfSpeak is a modern REST API for managing books and their reviews, designed for commercial-grade reliability, security, and extensibility. Built with Spring Boot, JPA, and JWT authentication, ShelfSpeak empowers users to share and discover book reviews with confidence.

> **Note:** Sentiment analysis for reviews (using external APIs like HuggingFace or Google NLP) is under development and will soon provide automatic insights on review tone.

## Base URL
```
http://localhost:8080
```

## Endpoint Security Summary
| Endpoint | Method | Public | Authenticated | Admin Only |
|----------|--------|--------|---------------|------------|
| /auth/** | any    | ✔      |               |            |
| /api/books | GET | ✔      |               |            |
| /api/books/{id} | GET | ✔  |               |            |
| /api/books/{id}/average-rating | GET | ✔ |           |            |
| /api/books | POST |        | ✔ (JWT)       |            |
| /api/books/{id} | DELETE |        |               | ✔ (JWT)   |
| /api/books/{bookId}/reviews | GET | ✔ |           |            |
| /api/books/{bookId}/reviews | POST |        | ✔ (JWT)       |            |
| /api/books/{bookId}/reviews/{reviewId} | PUT/DELETE |        | ✔ (JWT, owner or admin) |            |

> **Note:** For protected endpoints, include `Authorization: Bearer <token>` in the request header.

## Integration Test Status
- `AuthControllerIntegrationTest`: **complete & passing**
- `BookControllerIntegrationTest`: **complete & passing**
- `ReviewControllerIntegrationTest`: **complete & passing**

## Development Environment Setup

### Quick Start (Dev Profile)
When running with `spring.profiles.active=dev`, the application automatically creates a default admin user:

- **Username:** `admin`
- **Password:** `admin123`
- **Email:** `admin@bookreview.com`

### Testing Admin Features
1. Start the application with dev profile: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
2. Login with admin credentials: `POST /auth/login`
3. Use the returned JWT token for admin-only endpoints (e.g., DELETE `/api/books/{id}`)

### H2 Console Access
- **URL:** `http://localhost:8080/h2-console`
- **JDBC URL:** `jdbc:h2:mem:bookreviewdb`
- **Username:** (empty)
- **Password:** (empty)

> **Security Note:** Change the default admin credentials immediately if using for anything beyond local development.

## Available Endpoints

### Book Management

#### 1. Create a Book
**POST** `/api/books`

**Request Body:**
```json
{
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction"
}
```

**Response:**
```json
{
    "id": 1,
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction"
}
```

#### 2. Get All Books
**GET** `/api/books`

**Response:**
```json
[
    {
        "id": 1,
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
        "genre": "Fiction"
    }
]
```

#### 3. Get Book by ID
**GET** `/api/books/{id}`

**Response:**
```json
{
    "id": 1,
    "title": "The Great Gatsby",
    "author": "F. Scott Fitzgerald",
    "genre": "Fiction"
}
```

#### 4. Delete Book
**DELETE** `/api/books/{id}`

**Response:** `204 No Content`

#### 5. Get Average Rating
**GET** `/api/books/{id}/average-rating`

**Response:**
```json
4.5
```

### Review Management

#### 1. Add Review to Book
**POST** `/api/books/{bookId}/reviews`

**Request Body:**
```json
{
    "comment": "Excellent book! Highly recommended.",
    "rating": 5
}
```

**Response:**
```json
{
    "id": 1,
    "reviewerName": "John Doe",
    "userId": 2,
    "comment": "Excellent book! Highly recommended.",
    "rating": 5,
    "createdAt": "2025-07-06T12:00:00",
    "updatedAt": "2025-07-06T12:00:00"
}
```

#### 2. Get All Reviews for a Book
**GET** `/api/books/{bookId}/reviews`

**Response:**
```json
[
    {
        "id": 1,
        "reviewerName": "John Doe",
        "userId": 2,
        "comment": "Excellent book! Highly recommended.",
        "rating": 5,
        "createdAt": "2025-07-06T12:00:00",
        "updatedAt": "2025-07-06T12:00:00"
    },
    {
        "id": 2,
        "reviewerName": "Jane Smith",
        "userId": 3,
        "comment": "Good read, but could be better.",
        "rating": 4,
        "createdAt": "2025-07-06T12:05:00",
        "updatedAt": "2025-07-06T12:05:00"
    }
]
```

## Error Responses

### 400 Bad Request
```json
{
    "timestamp": "2025-06-29T13:00:26.608575",
    "status": 400,
    "error": "Bad Request",
    "message": "Validation failed: Title cannot be empty",
    "path": "/api/books"
}
```

### 404 Not Found
```json
{
    "timestamp": "2025-06-29T13:00:26.608575",
    "status": 404,
    "error": "Not Found",
    "message": "Book not found with ID: 999",
    "path": "/api/books/999"
}
```

### 409 Conflict
```json
{
    "timestamp": "2025-06-29T13:00:26.608575",
    "status": 409,
    "error": "Conflict",
    "message": "Book already exists with title: The Great Gatsby and author: F. Scott Fitzgerald",
    "path": "/api/books"
}
```

### 503 Service Unavailable
```json
{
    "timestamp": "2025-06-29T13:00:26.608575",
    "status": 503,
    "error": "Service Unavailable",
    "message": "Database operation failed: save book",
    "path": "/api/books"
}
```
> **Note:** 503 errors are rare and usually indicate a database or internal server error. Check logs for details.

## Monitoring Endpoints

### Health Check
**GET** `/actuator/health`

**Response:**
```json
{
    "status": "UP",
    "components": {
        "db": {
            "status": "UP",
            "details": {
                "database": "MySQL",
                "validationQuery": "isValid()"
            }
        },
        "diskSpace": {
            "status": "UP",
            "details": {
                "total": 499963174912,
                "free": 499963174912,
                "threshold": 10485760
            }
        }
    }
}
```

### API Mappings
**GET** `/actuator/mappings`

Shows all available endpoints and their handlers.

### Application Info
**GET** `/actuator/info`

Shows application information.

## Database Console

### H2 Console
**URL:** `http://localhost:8080/h2-console`

**Connection Details:**
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

## Testing the API

### Using curl

1. **Create a book:**
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Book","author":"Test Author","genre":"Test Genre"}'
```

2. **Get all books:**
```bash
curl http://localhost:8080/api/books
```

3. **Add a review:**
```bash
curl -X POST http://localhost:8080/api/books/1/reviews \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{"comment":"Great book!","rating":5}'
```

## Validation Rules

| Field         | Entity  | Required | Constraints/Notes                       |
|--------------|---------|----------|-----------------------------------------|
| title        | Book    | Yes      | Not empty, max 255 chars                |
| author       | Book    | Yes      | Not empty, max 255 chars                |
| genre        | Book    | Yes      | Not empty                               |
| comment      | Review  | Yes      | Not empty, max 1000 chars               |
| rating       | Review  | Yes      | Integer, 1–5 inclusive                  |
| user         | Review  | Yes      | Must be authenticated user, username not null/empty |
| createdAt    | Review  | Auto     | Set automatically                       |
| updatedAt    | Review  | Auto     | Set automatically                       |

- All fields are validated via DTOs and service-layer checks.
- Error responses include details for validation failures.