package com.atc.controller;

import com.atc.model.Flight.FlightStatus;
import com.atc.service.FlightService;
import com.atc.service.RunwayService;
import com.atc.service.UserService;
import com.atc.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collection;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final FlightService flightService;
    private final RunwayService runwayService;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/users/login";
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        for (GrantedAuthority authority : authorities) {
            String role = authority.getAuthority();

            if ("ROLE_ADMINISTRATOR".equals(role)) {
                model.addAttribute("totalUsers",    userService.getAllUsers().size());
                model.addAttribute("pilotCount",    userService.countByRole(User.Role.PILOT));
                model.addAttribute("atcCount",      userService.countByRole(User.Role.ATC_CONTROLLER));
                model.addAttribute("adminCount",    userService.countByRole(User.Role.ADMINISTRATOR));
                model.addAttribute("totalFlights",  flightService.getAllFlights().size());
                model.addAttribute("totalRunways",  runwayService.getAllRunways().size());
                model.addAttribute("availableRunways", runwayService.countByStatus(
                    com.atc.model.Runway.RunwayStatus.AVAILABLE));
                model.addAttribute("users",         userService.getAllUsers());
                return "dashboard/admin";

            } else if ("ROLE_ATC_CONTROLLER".equals(role)) {
                model.addAttribute("pendingCount",
                    flightService.getFlightsByStatus(FlightStatus.PENDING).size());
                model.addAttribute("activeCount",
                    flightService.getFlightsByStatus(FlightStatus.IN_PROGRESS).size());
                model.addAttribute("emergencyCount",
                    flightService.getFlightsByStatus(FlightStatus.EMERGENCY).size());
                model.addAttribute("availableRunways",
                    runwayService.getAvailableRunways().size());
                return "dashboard/atc";

            } else if ("ROLE_PILOT".equals(role)) {
                model.addAttribute("totalFlights",    flightService.getAllFlights().size());
                model.addAttribute("pendingFlights",
                    flightService.getFlightsByStatus(FlightStatus.PENDING).size());
                model.addAttribute("approvedFlights",
                    flightService.getFlightsByStatus(FlightStatus.APPROVED).size());
                model.addAttribute("rejectedFlights",
                    flightService.getFlightsByStatus(FlightStatus.REJECTED).size());
                return "dashboard/pilot";
            }
        }
        return "redirect:/flights";
    }
}
