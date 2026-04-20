# ATC System — Design & Structural Patterns

## System Architecture Overview

```
┌──────────┐   HTTP/Form    ┌────────────────┐   calls   ┌──────────────┐   JPA   ┌────────┐
│ Browser  │ ─────────────► │  Controller    │ ────────► │   Service    │ ──────► │  H2 DB │
│(Thymeleaf│ ◄───────────── │  Layer         │ ◄──────── │   Layer      │ ◄────── │        │
│  HTML)   │  Template      └────────────────┘   data    └──────────────┘         └────────┘
└──────────┘  Rendering           │                             │
                                  │ Spring Security             │
                                  ▼                             ▼
                        ┌─────────────────┐           ┌──────────────┐
                        │  SecurityConfig  │           │  Repository  │
                        │  (RBAC rules)   │           │  Interfaces  │
                        └─────────────────┘           └──────────────┘
```

---

## 1. 🏗 Model–View–Controller (MVC) — *Architectural*

The primary architecture of the entire application.

| Layer | Files | Responsibility |
|---|---|---|
| **Model** | `User`, `Pilot`, `ATCController`, `Administrator`, `Flight`, `Runway` | Data representation + JPA entities |
| **View** | All `templates/*.html` (Thymeleaf) | User interface and presentation |
| **Controller** | `FlightController`, `RunwayController`, `UserController`, `DashboardController` | Handle HTTP requests, connect Model ↔ View |

**Flow example:**
```
GET /flights  →  FlightController.listFlights()
                    → flightService.getAllFlights()     [Model]
                    → model.addAttribute("flights", …)
                    → return "flight/list"              [View: flight/list.html]
```

---

## 2. 🧱 Facade — *Structural*

Each Service class provides a **simplified interface** to the Controller, hiding internal complexity (validation, multi-repository operations, JPA transactions).

```
FlightController  —calls→  FlightService  —uses→  FlightRepository
                              (Facade)               (complex JPA)

RunwayController  —calls→  RunwayService  —uses→  RunwayRepository
                              (Facade)             + FlightRepository
```

**Example — `RunwayService.allocateRunway()` hides a multi-step operation:**
```java
public Runway allocateRunway(String runwayId, String flightId) {
    Runway runway = runwayRepository.findByRunwayId(runwayId)…;
    Flight flight = flightRepository.findByFlightId(flightId)…;
    runway.assignRunway();       // validates availability
    flight.setRunway(runway);    // links entities
    runwayRepository.save(runway);
    flightRepository.save(flight);
    return runway;
}
// Controller just calls: runwayService.allocateRunway(runwayId, flightId)
```

---

## 3. 📐 Template Method — *Behavioural*

`User.java` is an **abstract class** that defines the *skeleton* of the login/logout workflow. Subclasses must fill in the concrete steps:

```java
// User.java — defines the algorithm skeleton
public abstract void login();
public abstract void logout();
```

```java
// Pilot.java — concrete implementation
@Override
public void login()  { System.out.println("Pilot "  + getName() + " logged in."); }
@Override
public void logout() { System.out.println("Pilot "  + getName() + " logged out."); }

// ATCController.java
@Override
public void login()  { System.out.println("ATC Controller " + getName() + " logged in."); }
```

---

## 4. 🔀 Strategy — *Behavioural*

Spring Security's `PasswordEncoder` is a **Strategy interface**. The algorithm (BCrypt) is injected as a concrete strategy and can be swapped at any time without touching the rest of the code:

```java
// SecurityConfig.java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();  // ← swap with MD5, Argon2, etc.
}

// UserService.java — depends on the interface, not the concrete class
private final PasswordEncoder passwordEncoder;   // Strategy interface
// usage:
passwordEncoder.encode(rawPassword);
```

---

## 5. 🔁 Singleton — *Creational*

All Spring-managed beans (`@Service`, `@Repository`, `@Controller`, `@Configuration`) are **Singletons** by default — the IoC container creates exactly one instance per class and reuses it across all requests. This is thread-safe and eliminates manual lifecycle management.

```java
@Service                     // → Singleton managed by Spring
public class FlightService { … }

@Repository                  // → Singleton managed by Spring
public interface FlightRepository extends JpaRepository<Flight, Long> { … }
```

---

## 6. 🧬 Inheritance + JPA JOINED Table Strategy — *Structural / ORM*

The `User` entity hierarchy uses `@Inheritance(strategy = InheritanceType.JOINED)` — each subclass gets its own DB table, linked by foreign key. This maps the OOP hierarchy directly to the relational model.

```
           ┌─────────────────────┐
           │   User  (abstract)  │  ← table: users (id, name, email, role, password)
           └──────────┬──────────┘
       ┌──────────────┼──────────────┐
       ▼              ▼              ▼
   Pilot          ATCController  Administrator
(table: pilots)  (atc_controllers) (administrators)
(license_number) (station_code)   (no extra cols)
    FK→users         FK→users         FK→users
```

**Follows Liskov Substitution Principle (LSP):** `UserRepository<User>` works with all subtypes:
```java
userRepository.findAll()  // returns Pilots, ATCControllers, Administrators transparently
```

---

## 7. 🔐 Role-Based Access Control (RBAC) — *Security Pattern*

Permissions are assigned to **roles**, not individual users. Spring Security enforces this in `SecurityConfig` using HTTP-method-level rules:

```java
// GET pages: all authenticated users can view
.requestMatchers(HttpMethod.GET, "/flights", "/flights/submit", "/runways").authenticated()

// POST mutations: strictly per role
.requestMatchers(HttpMethod.POST, "/flights/submit").hasRole("PILOT")
.requestMatchers(HttpMethod.POST, "/flights/*/approve", "/runways/assign").hasRole("ATC_CONTROLLER")
.requestMatchers(HttpMethod.POST, "/runways/create", "/users/create").hasRole("ADMINISTRATOR")
```

| Role | Create Flight | Approve/Reject | Assign Runway | Create Runway | Manage Users |
|---|:---:|:---:|:---:|:---:|:---:|
| PILOT | ✅ | ❌ | ❌ | ❌ | ❌ |
| ATC_CONTROLLER | ❌ | ✅ | ✅ | ❌ | ❌ |
| ADMINISTRATOR | ❌ | ❌ | ❌ | ✅ | ✅ |

---

## 8. 🛡 Fail-Safe UI (Defensive UI Pattern) — *UI / UX Pattern*

Rather than purely relying on server-side 403 errors, the UI proactively hides or disables elements the current role can't use. This is a defence-in-depth approach: both layers protect the system.

**Layer 1 — Hide sidebar links (Thymeleaf `sec:authorize`):**
```html
<a href="/runways/assign" sec:authorize="hasRole('ATC_CONTROLLER')">Assign Runway</a>
<!-- Pilots and Admins never see this link -->
```

**Layer 2 — Show locked card instead of form:**
```html
<div sec:authorize="!hasRole('PILOT')">
    <div class="lock-card">🔒 Pilots Only</div>
</div>
<div sec:authorize="hasRole('PILOT')">
    <form>…</form>   <!-- actual form -->
</div>
```

**Layer 3 — Disable buttons contextually:**
```html
<!-- Release button disabled if runway isn't OCCUPIED -->
<button th:disabled="${r.status.name() != 'OCCUPIED'}">↩ Release</button>

<!-- Delete button disabled if runway is still in use -->
<button th:disabled="${r.status.name() == 'OCCUPIED'}">🗑 Delete</button>
```

**Layer 4 — Styled 403/404/500 error page** (`CustomErrorController`) instead of Spring's white-label error page.

---

## 9. 🗄 Repository Pattern — *Data Access*

Data access is abstracted behind `JpaRepository<T, ID>` interfaces. No SQL is written — the repository derives queries from method names or uses Spring Data conventions:

```java
// FlightRepository.java
public interface FlightRepository extends JpaRepository<Flight, Long> {
    List<Flight> findByStatus(FlightStatus status);   // derived query
    Optional<Flight> findByFlightId(String flightId); // derived query
    List<Flight> findByPilotUserId(Long pilotId);
}

// RunwayRepository.java
public interface RunwayRepository extends JpaRepository<Runway, Long> {
    List<Runway> findByStatus(RunwayStatus status);
    Optional<Runway> findByRunwayId(String runwayId);
}
```

---

## 10. 🌱 Startup Seeder (Initialization Pattern) — *Creational*

`DataInitializer` implements Spring's `CommandLineRunner`. It runs **once on startup** and seeds default data only if the DB is empty — safe to use with the ephemeral H2 in-memory database:

```java
@Component
public class DataInitializer implements CommandLineRunner {
    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            // seed admin, atc, pilot users
        }
        if (runwayRepository.count() == 0) {
            // seed 6 runways: RWY-09L/R, RWY-27L/R, RWY-18, RWY-36
        }
    }
}
```

---

## Master Summary Table

| # | Pattern | GoF Category | Where Used |
|---|---|---|---|
| 1 | **MVC** | Architectural | Entire system (Controllers / Thymeleaf / JPA entities) |
| 2 | **Facade** | Structural | `FlightService`, `RunwayService`, `UserService` |
| 3 | **Template Method** | Behavioural | `User.java` abstract `login()` / `logout()` |
| 4 | **Strategy** | Behavioural | `PasswordEncoder` in `SecurityConfig` |
| 5 | **Singleton** | Creational | All Spring `@Service`, `@Repository`, `@Controller` beans |
| 6 | **Inheritance (JOINED)** | Structural + ORM | `User → Pilot / ATCController / Administrator` |
| 7 | **RBAC** | Security | `SecurityConfig` HTTP method-level rules |
| 8 | **Fail-Safe UI** | Defensive UI | `sec:authorize` in templates + `CustomErrorController` |
| 9 | **Repository** | Data Access | `FlightRepository`, `RunwayRepository`, `UserRepository` |
| 10 | **Startup Seeder** | Creational | `DataInitializer implements CommandLineRunner` |

> [!NOTE]
> Patterns 1–5 are classic GoF (Gang of Four) patterns applied via Spring Boot's built-in mechanisms.
> Patterns 6–10 are domain/framework-specific patterns that emerge from JPA, Spring Security, and the system's aviation domain.
