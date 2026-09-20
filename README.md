# PhotoClone API

PhotoClone is a Spring Boot REST API for registering users and managing image metadata and file content. It uses Spring Security with database-backed HTTP Basic authentication, BCrypt password hashing, JPA, and an H2 database for local development.

## Features

- User registration with duplicate username and email protection
- BCrypt password hashing and database-backed authentication
- Authenticated photo upload, listing, search, update, download, and deletion
- Multipart upload validation for image content types
- OpenAPI documentation through Swagger UI
- Unit tests with Mockito and integration tests with MockMvc

## Requirements

- Java 21
- Maven 3.9+ or the included Maven wrapper

## Run Locally

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

The API is available at `http://localhost:8080`.

The default database is an in-memory H2 database. Demo users are not created automatically. To seed the local demo users, run with `--photoclone.seed-data=true`; change or remove those credentials before using a shared environment.

## API

### Public endpoints

| Method | Path | Description |
| --- | --- | --- |
| `POST` | `/api/users/register` | Register a user |
| `GET` | `/swagger-ui/index.html` | OpenAPI UI |
| `GET` | `/v3/api-docs` | OpenAPI JSON |

### Authenticated endpoints

All photo and user-list endpoints require HTTP Basic credentials for a registered user.

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/api/users` | List users |
| `POST` | `/api/photos/upload` | Upload an image as multipart form data |
| `GET` | `/api/photos` | List photos |
| `GET` | `/api/photos/{id}` | Read photo metadata |
| `GET` | `/api/photos/{id}/image` | Download image bytes |
| `GET` | `/api/photos/search?q=term` | Search descriptions |
| `PUT` | `/api/photos/{id}` | Update description and tags |
| `DELETE` | `/api/photos/{id}` | Delete a photo |

Example registration:

```bash
curl -X POST http://localhost:8080/api/users/register \
	-H "Content-Type: application/json" \
	-d '{"username":"alice","password":"strong-password","email":"alice@example.com","fullName":"Alice"}'
```

Example authenticated request:

```bash
curl -u alice:strong-password http://localhost:8080/api/photos
```

## Security Notes

- Passwords are stored as BCrypt hashes and are never returned as plaintext.
- All API routes are protected except registration and documentation.
- H2 console access is intended for local development only.
- The default in-memory database is not suitable for production persistence.
- Use HTTPS, externalized secrets, a production database, and a stronger token-based authentication flow for deployment.

## Testing

Run the complete test suite:

```bash
./mvnw test
```

The suite includes service unit tests and Spring Boot integration tests covering authentication and registration.

## Project Layout

```text
src/main/java/com/daniel/photoclone
	config/       Security configuration
	controller/   REST endpoints
	model/        JPA entities
	repository/   Spring Data repositories
	service/      Application and validation logic
src/test/java   Unit and integration tests
```

## License

This project is provided for learning and portfolio use. Add a license before distributing it as a library or hosted service.
