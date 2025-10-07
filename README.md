<img width="336" height="300" alt="image" src="https://github.com/user-attachments/assets/82f0c0b3-b6cf-464e-a038-1162feae54b2" />

# Rent My Car - REST API

A Kotlin REST API with the Ktor & Exposed frameworks.

## Table of Contents

- [Introduction](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#introduction)
- [Getting Started](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#getting-started)
- [Usage](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#usage)
- [Endpoints](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#endpoints)
- [Contributing](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#contributing)
- [License](https://github.com/raphaelz2/AvansAPI/blob/master/README.md#license)

## Introduction

Rent My Car API powers a sleek and user-friendly mobile/SPA experience for users who want quick, hassle-free car rentals—or wish to list their own vehicles.
The service exposes endpoints for users, cars, and reservations, with a simple domain model and battle-tested auth.

## Tech highlights
•	Ktor 3 (Netty) + Content Negotiation (JSON)
•	Exposed (SQL DSL) with transactions
•	SQLite (file DB) via HikariCP
•	Flyway migrations
•	JWT (Bearer) authentication
•	Optional Swagger UI at /swagger
•	Passwords stored as bcrypt hashes (no plaintext)



## Getting Started

Prerequisites
•	JDK 21+ (Corretto/Temurin recommended)
•	Gradle (wrapper included)
•	IntelliJ IDEA (optional but recommended)


Follow these steps to set up and run the AvansAPI project locally:

1. Clone the Repository

- Access the GitHub repository: AvansAPI

- Clone it or download the ZIP and extract it to a convenient location on your machine.

2. Open the Project

- Launch IntelliJ IDEA.

- Open the extracted folder (AvansAPI) as a new project.

Configure (if needed)
Default config is in src/main/resources/application.conf.
```Key bits:
  app { useFake = false }             # true = in-memory repos (no DB)
  db {
    driver = "org.sqlite.JDBC"
    jdbcUrl = "jdbc:sqlite:data/app.db"  # DB file; folder created on startup
  }
  
  jwt {
    issuer = "http://localhost:8080/"
    audience = "jwt-audience"
    realm = "Access to 'me'"
    secret = "change-me"              # change for prod!
  }

```
3. Configure and Run 

- Ensure your development environment is set up and all project dependencies are resolved.

- Run the application by executing the main() function in Application.kt.
  Or Run it using Gradle after making a new Gradle run configuration:
  ``` run
    # Windows
  gradlew.bat clean run
  # macOS/Linux
  ./gradlew clean run
  ```
## Server starts on http://localhost:8080.
If the ```data/``` folder didn’t exist, it will be created automatically and the DB initialized by Flyway.

## (Optional) Swagger UI
If enabled with the Docs.kt plugin:
•	Swagger UI: http://localhost:8080/swagger
•	OpenAPI JSON: http://localhost:8080/api.json


4. Explore Predefined HTTP Requests

- Once the app is running, you can interact with predefined HTTP requests for entities such as Rental, User, and Vehicle.

- Requests are organized in subdirectories according to entity type.
- 
## Usage

## 1) Seed an Admin (one-time)

Create a first user directly in SQLite (or via the API). Example SQL (bcrypt hash for "secret"):
```
 INSERT INTO users (first_name,last_name,password,email,created_at,modified_at)
VALUES ('Admin','User',
'$2b$12$TOyY6LBDqJZtfef4w/3DWuPfGH65B94bSlpEkyBk0o6bSk0rzNOPO',
'admin@example.com','2025-09-20T12:00','2025-09-20T12:00');
```
## 2) Login (get JWT)

## With Postman (recommended)
```
POST {{baseUrl}}/login
{
  "email": "admin@example.com",
  "password": "secret"
}
```
``` Success (200):
{ "token": "eyJhbGciOiJIUzI1NiIs..." }
```
Save the token as a Bearer Token at the Collection level (Authorization tab → Bearer Token).

## 3) Calling protected endpoints
For all secured endpoints below, add Authorization: Bearer <your-token> (setting it at the Collection level is easiest).

## Endpoints
 ## Me (verify token)
  •	GET {{baseUrl}}/me
``` 200 Response (text or JSON depending on your implementation):
Hello, Admin! Token expires in 598000 ms.
```
 ## Auth
  •	POST /login — returns { "token": "<jwt>" }
  •	GET /me — verifies token and returns a greeting

 ## Cars
  •	GET /cars — list cars
  •	GET /cars/{id} — get by id
  •	POST /cars — create
  •	PUT /cars/{id} — update
  •	DELETE /cars/{id} — delete
 ## Users
  •	GET /users — list users
  •	GET /users/{id} — get by id
  •	POST /users — create (stores hashed password)
  •	PUT /users/{id} — update (non-password fields)
  •	DELETE /users/{id} — delete
  ## Reservations
  •	GET /reservations — list reservations
  •	GET /reservations/{id} — get by id
  •	POST /reservations — create
  •	PUT /reservations/{id} — update
  •	DELETE /reservations/{id} — delete
  
  ## Endpoint examples
  ## Cars
   ``` List cars
    
    GET {{baseUrl}}/cars
    200 Response:
    
    {
      "GetCarResponseList": [
        {
          "id": 1,
          "make": "Tesla",
          "model": "Model 3",
          "price": 59.9,
          "pickupLocation": "Eindhoven",
          "category": "sedan",
          "powerSourceType": "BEV",
          "imageFileNames": [],
          "createdAt": "2025-09-20T12:00",
          "modifiedAt": "2025-09-20T12:00"
        }
      ]
    }
```
``` Create car
POST {{baseUrl}}/cars
Body:

{
  "make": "Tesla",
  "model": "Model 3",
  "price": 59.9,
  "pickupLocation": "Eindhoven",
  "category": "sedan",
  "powerSourceType": "BEV",
  "imageFileNames": [],
  "createdAt": "2025-09-20T12:00",
  "modifiedAt": "2025-09-20T12:00"
}
```
## Users
``` List users
GET {{baseUrl}}/users
200 Response:
{
  "GetUsersResponseList": [
    {
      "id": 1,
      "firstName": "Admin",
      "lastName": "User",
      "email": "admin@example.com",
      "createdAt": "2025-09-20T12:00",
      "modifiedAt": "2025-09-20T12:00"
    }
  ]
}
```
``` Create user
POST {{baseUrl}}/users
Body:

{
  "firstName": "Admin",
  "lastName": "User",
  "password": "secret",
  "email": "admin@example.com",
  "createdAt": "2025-09-20T12:00",
  "modifiedAt": "2025-09-20T12:00"
}
```

## Contributing
Contributions are welcome!
1.	Fork the repo & create a feature branch.
2.	Keep code formatted and idiomatic (Kotlin).
3.	Add/adjust tests where relevant.
4.	Open a PR with a clear description.
## Dev tips
•	Run locally with ./gradlew run.
•	DB lives at data/app.db (SQLite). You can inspect it via DB Browser for SQLite / IntelliJ Database tool.
•	Toggle fake vs SQL repos in application.conf (app.useFake = true|false).

## License
This project is licensed under the MIT License.
You’re free to use, copy, modify, merge, publish, and distribute with attribution and without warranty.
If you prefer a different license (e.g., Apache-2.0), replace this section and add the appropriate LICENSE file.







