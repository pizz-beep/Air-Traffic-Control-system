package com.atc.config;

import com.atc.model.ATCController;
import com.atc.model.Administrator;
import com.atc.model.Pilot;
import com.atc.model.Runway;
import com.atc.model.User;
import com.atc.repository.RunwayRepository;
import com.atc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RunwayRepository runwayRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // ── Seed Users ───────────────────────────────────────────────
        if (userRepository.count() == 0) {
            Administrator admin = new Administrator();
            admin.setName("System Admin");
            admin.setEmail("admin@atc.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMINISTRATOR);
            userRepository.save(admin);

            ATCController atc = new ATCController();
            atc.setName("ATC Tower 1");
            atc.setEmail("atc@atc.com");
            atc.setPassword(passwordEncoder.encode("atc123"));
            atc.setRole(User.Role.ATC_CONTROLLER);
            atc.setStationCode("JFK-TWR");
            userRepository.save(atc);

            Pilot pilot = new Pilot();
            pilot.setName("Captain Smith");
            pilot.setEmail("pilot@atc.com");
            pilot.setPassword(passwordEncoder.encode("pilot123"));
            pilot.setRole(User.Role.PILOT);
            pilot.setLicenseNumber("PL-9876543");
            userRepository.save(pilot);

            System.out.println("✅ Initial Users Seeded:");
            System.out.println("   Admin:  admin@atc.com / admin123");
            System.out.println("   ATC:    atc@atc.com   / atc123");
            System.out.println("   Pilot:  pilot@atc.com / pilot123");
        }

        // ── Seed Runways ─────────────────────────────────────────────
        if (runwayRepository.count() == 0) {
            runwayRepository.save(makeRunway("RWY-09L", "09L", 3200, Runway.RunwayStatus.AVAILABLE));
            runwayRepository.save(makeRunway("RWY-09R", "09R", 2800, Runway.RunwayStatus.AVAILABLE));
            runwayRepository.save(makeRunway("RWY-27L", "27L", 3200, Runway.RunwayStatus.AVAILABLE));
            runwayRepository.save(makeRunway("RWY-27R", "27R", 2800, Runway.RunwayStatus.OCCUPIED));
            runwayRepository.save(makeRunway("RWY-18",  "18",  2500, Runway.RunwayStatus.MAINTENANCE));
            runwayRepository.save(makeRunway("RWY-36",  "36",  2500, Runway.RunwayStatus.AVAILABLE));

            System.out.println("✅ 6 Runways Seeded (RWY-09L/R, RWY-27L/R, RWY-18, RWY-36)");
        }
    }

    private Runway makeRunway(String id, String direction, int length, Runway.RunwayStatus status) {
        Runway r = new Runway();
        r.setRunwayId(id);
        r.setDirection(direction);
        r.setLengthMeters(length);
        r.setStatus(status);
        return r;
    }
}
