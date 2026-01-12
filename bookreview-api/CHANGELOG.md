# Changelog

All notable changes to ShelfSpeak will be documented in this file.

## [Unreleased]
- Future enhancements and improvements planned for ShelfSpeak.

## [2025-07-12]
### Added
- **DevDataInitializer**: Implemented automatic admin user creation for development environment using `@PostConstruct` and `CommandLineRunner` with `@Profile("dev")`.
- **Development Environment Setup**: Auto-creates default admin user (username: `admin`, password: `admin123`) when running with dev profile.
- **Enhanced Documentation**: Updated API documentation with dev environment setup instructions and H2 console access details.
- **Postman Collection Updates**: Added "Authentication & Admin" folder with ready-to-use requests for admin login and user registration.
- **Production Deployment Guide**: Added comprehensive documentation for environment variables and security best practices.


### Technical Details
- Dev environment now supports full role-based access testing out-of-the-box
- H2 console accessible at `http://localhost:8080/h2-console` for database inspection
- All integration tests now properly use test profile isolation
- JWT configuration documented for all environments (dev, test, prod)

## [2025-07-07]
### Fixed
- Fixed a bug where review creation failed due to missing timestamps (`createdAt`, `updatedAt`).
- Fixed a bug where reviews could be created with a null or empty username, causing validation errors and inconsistent data. Now, reviews require a non-empty username for the associated user.

### Added
- Integration tests now fully cover authentication and book endpoints, including security and error handling.
- Updated documentation to clarify endpoint security and test output access.

## [2025-06-25]
### Fixed
- Fixed a **LAZY LOADING** issue: Switched the `reviews` relationship in the `Book` entity to `FetchType.EAGER` to ensure reviews are loaded with books, preventing `LazyInitializationException` during serialization and in controller responses. 