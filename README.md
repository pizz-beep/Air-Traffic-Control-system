# Air Traffic Control System (ATC)

A full-stack **Air Traffic Control (ATC) Management System** built using **Spring Boot (MVC architecture)** to simulate real-world aircraft operations including flight planning, runway allocation, and airspace monitoring.

This system enables seamless interaction between **Pilots, Air Traffic Controllers, and Administrators**, ensuring safe and efficient air traffic operations.

---

## Architecture Overview

The system strictly follows the **MVC (Model–View–Controller)** pattern:

| Layer          | Component           | Responsibility                       |
| -------------- | ------------------- | ------------------------------------ |
| **Model**      | Entities & Services | Data representation + business logic |
| **View**       | Thymeleaf Templates | UI / Presentation                    |
| **Controller** | Spring Controllers  | Request handling                     |

---

## Actors

* **Pilot**
* **Air Traffic Controller (ATC)**
* **Administrator**

---

## Core Functionalities

### Major Use Cases

* Submit Flight Plan (Pilot)
* Approve / Reject Flight Plan (ATC)
* Assign Runway (ATC)
* Monitor Aircraft Status (ATC)

### Minor Use Cases

* Login / Authentication
* Update Flight Status
* Generate Reports
* User Management

---

## Project Structure

```bash
atc-system/
├── src/main/java/com/atc/
│   ├── model/
│   │   ├── User.java
│   │   ├── Pilot.java
│   │   ├── ATCController.java
│   │   ├── Administrator.java
│   │   ├── Flight.java
│   │   └── Runway.java
│   ├── service/
│   │   ├── FlightService.java
│   │   ├── RunwayService.java
│   │   └── UserService.java
│   ├── controller/
│   │   ├── FlightController.java
│   │   ├── RunwayController.java
│   │   └── UserController.java
│   ├── repository/
│   │   ├── FlightRepository.java
│   │   ├── RunwayRepository.java
│   │   └── UserRepository.java
│
├── src/main/resources/
│   ├── templates/
│   │   ├── flight/
│   │   │   ├── submit.html
│   │   │   └── list.html
│   │   ├── runway/
│   │   │   └── assign.html
│   │   └── dashboard.html
│   └── application.properties
│
└── pom.xml
```

---

## Tech Stack

* **Backend:** Java, Spring Boot
* **Frontend:** Thymeleaf + HTML + Bootstrap
* **Database:** H2 (in-memory)
* **ORM:** JPA (Hibernate)
* **Security:** Spring Security
* **Build Tool:** Maven

---

## Model Layer

Defines system entities and business logic:

* `User` (Abstract Base Class with roles)
* `Pilot`
* `ATCController`
* `Administrator`
* `Flight`
* `Runway`

### Key Features:

* Inheritance mapping using JPA
* Enum-based role and status handling
* Flight lifecycle management
* Runway availability control

---

## Service Layer

Handles business operations:

* **FlightService**

  * Submit flight plans
  * Approve / Reject flights
  * Update flight status

* **RunwayService**

  * Allocate runway
  * Release runway
  * Track availability

* **UserService**

  * Register users
  * Encrypt passwords
  * Manage users

---

## Controller Layer

Handles HTTP requests and connects View + Model:

* `FlightController`
* `RunwayController`
* `UserController`

### Example Endpoints:

* `GET /flights` → List flights
* `POST /flights/submit` → Submit flight
* `POST /flights/{id}/approve` → Approve flight
* `POST /runways/assign` → Assign runway

---

## View Layer (Thymeleaf)

UI templates for system interaction:

* Flight submission form
* Flight monitoring dashboard
* Runway assignment interface

Built using **Bootstrap for responsive UI**.

---

## Security Configuration

* Role-based access using Spring Security:

  * `PILOT`
  * `ATC_CONTROLLER`
  * `ADMINISTRATOR`

### Access Control:

* Pilots → Submit flights
* ATC → Approve + Assign runway
* Admin → Manage users

---

## Database Configuration

Uses **H2 in-memory database** for development:

```properties
spring.datasource.url=jdbc:h2:mem:atcdb
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=update
```

---

## Running the Application

### Prerequisites

* Java 17+
* Maven

### Steps

```bash
git clone https://github.com/your-username/atc-system.git
cd atc-system
mvn spring-boot:run
```

Access the app at:

```
http://localhost:8080
```

---

## API Endpoint Summary

| Method | Endpoint              | Actor | Description      |
| ------ | --------------------- | ----- | ---------------- |
| GET    | /flights              | All   | View flights     |
| POST   | /flights/submit       | Pilot | Submit plan      |
| POST   | /flights/{id}/approve | ATC   | Approve          |
| POST   | /flights/{id}/reject  | ATC   | Reject           |
| GET    | /flights/monitor      | ATC   | Monitor aircraft |
| GET    | /runways              | ATC   | View runways     |
| POST   | /runways/assign       | ATC   | Assign runway    |
| GET    | /users                | Admin | View users       |

---

## Key Highlights

* Strict **MVC architecture implementation**
* Real-world system simulation (aviation domain)
* Role-based security with Spring Security
* Clean separation of concerns (Model, Service, Controller)
* Extensible and scalable design

## Design Patterns & Principles Applied

### Design Principles
1. **Single Responsibility Principle (SRP)**: Clean separation of concerns is maintained. Each layer handles precisely one responsibility—Controllers handle HTTP requests, Services execute business logic, and Repositories manage database interaction.
2. **Open/Closed Principle (OCP)**: The abstract `User` base class serves as an extension point. System roles like `Pilot`, `ATCController`, and `Administrator` extend `User` without modifying the core user handling and authentication structure.
3. **Liskov Substitution Principle (LSP)**: Derived classes (`Pilot`, `ATCController`, `Administrator`) can be substituted for their base class (`User`) seamlessly within the repository and security layers.
4. **Dependency Inversion Principle (DIP)**: Controller and Service layers depend on abstractions (e.g., `JpaRepository` interfaces) injected by Spring rather than constructing concrete low-level implementation details themselves.

### Design Patterns
1. **Model-View-Controller (MVC) Pattern (Architectural)**: The overall application structure divides data handling, UI, and request processing strictly into Models, Thymeleaf Views, and Controllers.
2. **Template Method Pattern (Behavioral)**: The abstract `User.java` guarantees a blueprint defining `login()` and `logout()` signatures, enforcing subclasses to enact specific polymorphic behavioral implementations.
3. **Facade Pattern (Structural)**: Dedicated Service classes (`FlightService`, `RunwayService`) act as a Facade. They provide streamlined, simplified access interfaces to the Controllers, hiding the complex business validations and internal Repository operations.
4. **Strategy Pattern (Behavioral)**: `SecurityConfig` leverages the Strategy pattern through `PasswordEncoder`. The application provides `BCryptPasswordEncoder` as a concrete algorithm strategy injected securely.
5. **Singleton Pattern (Creational)**: Ensures `FlightService`, `RunwayService`, and Repositories are instantiated identically as thread-safe Singletons managed by the Spring IoC container.

---

## Future Enhancements

* Real-time aircraft tracking (WebSockets)
* Weather API integration
* AI-based runway allocation
* Emergency flight prioritization
* Dashboard analytics

---

## Collaborators:

* @SharmisthaAlike
* @aadhya2811
* @AditiUdaya
* @pizz-beep
