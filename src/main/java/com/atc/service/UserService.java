package com.atc.service;

import com.atc.model.*;
import com.atc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered.");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    /**
     * Creates and persists the correct User subclass based on the given role.
     *
     * @param name          display name
     * @param email         login email (must be unique)
     * @param rawPassword   plain-text password (will be BCrypt-encoded)
     * @param role          PILOT | ATC_CONTROLLER | ADMINISTRATOR
     * @param licenseNumber only used when role == PILOT
     * @param stationCode   only used when role == ATC_CONTROLLER
     */
    public User createUser(String name, String email, String rawPassword,
                           User.Role role, String licenseNumber, String stationCode) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered: " + email);
        }
        String encodedPw = passwordEncoder.encode(rawPassword);

        User newUser;
        switch (role) {
            case PILOT -> {
                Pilot p = new Pilot();
                p.setLicenseNumber(licenseNumber);
                newUser = p;
            }
            case ATC_CONTROLLER -> {
                ATCController a = new ATCController();
                a.setStationCode(stationCode);
                newUser = a;
            }
            default -> newUser = new Administrator();
        }
        newUser.setName(name);
        newUser.setEmail(email);
        newUser.setPassword(encodedPw);
        newUser.setRole(role);
        return userRepository.save(newUser);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException(
                "User not found: " + email));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public long countByRole(User.Role role) {
        return userRepository.findAll().stream()
            .filter(u -> u.getRole() == role)
            .count();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        com.atc.model.User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
            user.getEmail(),
            user.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
