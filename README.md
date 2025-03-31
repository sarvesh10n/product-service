

# Setup

### Please refer project-documentation repository for entire setup.


## Base URL

```
http://localhost:8080
```

## Authentication

All API except get all products uses **OAuth 2.0 with JWT tokens** for authentication. Clients must include a **Bearer Token** in the `Authorization` header for protected endpoints.


## Database and Migrations
- This service utilizes **Spring Hibernate** for database queries, ensuring efficient ORM-based interactions.
- **Flyway** manages database schema migrations, enabling version-controlled updates and rollback support.
- Database changes are handled through Flyway migration scripts stored in the `db/migration` folder, ensuring smooth and structured updates without data loss.
- Schema updates are automatically applied at application startup, maintaining consistency across environments (Just need to create database separately as mentioned in start of doc).

## Endpoints

### Please refer `API Documentation` and `Postman Collection` Folder in `project-documentation` repository
