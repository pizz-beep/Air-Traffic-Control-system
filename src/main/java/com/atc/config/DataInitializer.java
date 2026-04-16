package com.atc.config;

import com.atc.model.ATCController;
import com.atc.model.Administrator;
import com.atc.model.Pilot;
import com.atc.model.User;
import com.atc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
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

            System.out.println("✅ Initial Users Seeded! You can login with:");
            System.out.println("   - Admin: admin@atc.com / admin123");
            System.out.println("   - ATC: atc@atc.com / atc123");
            System.out.println("   - Pilot: pilot@atc.com / pilot123");
        }
    }
}
