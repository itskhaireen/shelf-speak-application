# ShelfSpeak

*---because books finally speak™*

A Spring Boot REST API (In transition to Fullstack) for managing book reviews with JWT authentication, role-based access control, and comprehensive testing.

## 📚 Features
- ✅ Angular Integration (In Development)
- ✅ JWT Authentication & Authorization
- ✅ Role-based Access Control (ADMIN/USER)
- ✅ CRUD operations for Books & Reviews
- ✅ Comprehensive Unit & Integration Tests
- ✅ Swagger API Documentation
- ✅ Environment-specific configurations
- ✅ Robust Exception Handling
- ✅ Docker containerization
- ✅ Health monitoring with Spring Boot Actuator

## 🌐 Access Points

### Local Development
- **API**: `http://localhost:8080/api/`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console`
- **Health Check**: `http://localhost:8080/actuator/health`

## 🔐 Authentication

### Default Admin Credentials (Dev Environment)
- **Username**: `admin`
- **Password**: `admin123`
- **Email**: `admin@bookreview.com`

### JWT Token Usage
For all protected endpoints, include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 🧪 Testing
```bash
# Run all tests
./mvnw test

# Run integration tests only
./mvnw test -Dtest=*IntegrationTest

# Run unit tests only
./mvnw test -Dtest=*Test -Dtest=!*IntegrationTest
```

## 📖 Documentation
- **[API Documentation](API_DOCUMENTATION.md)** - Detailed API endpoints and examples
- **[Changelog](CHANGELOG.md)** - Recent updates and fixes

## 🔧 Development
- **Profiles**: `dev` (H2), `test` (H2), `prod` (MySQL)
- **Database**: H2 (test), MySQL (prod/dev)
- **Security**: Spring Security with JWT
- **Testing**: JUnit 5, Mockito, MockMvc
- **Container**: Multi-stage Docker build with health checks
- **Front-End**: Angular

## 📦 Postman Collection
Import `BookReviewAPI.postman_collection.json` for ready-to-use API requests with authentication.

## 🔄 Environment Profiles - FlyWay Migration

| Profile | Database | Use Case |
|---------|----------|----------|
| `dev` | MySQL | Local development |
| `test` | H2 (in-memory) | Automated testing |
| `prod` | MySQL | Production deployment |
