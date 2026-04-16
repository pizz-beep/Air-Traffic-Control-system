package com.atc.controller;

import com.atc.model.Flight;
import com.atc.model.Flight.FlightStatus;
import com.atc.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    // GET /flights  → list all flights
    @GetMapping
    public String listFlights(Model model) {
        model.addAttribute("flights", flightService.getAllFlights());
        return "flight/list";
    }

    // GET /flights/submit  → show submit form
    @GetMapping("/submit")
    public String submitForm(Model model) {
        model.addAttribute("flight", new Flight());
        return "flight/submit";
    }

    // POST /flights/submit  → handle form submission
    @PostMapping("/submit")
    public String submitFlightPlan(@ModelAttribute Flight flight) {
        flightService.createFlight(flight);
        return "redirect:/flights";
    }

    // POST /flights/{id}/approve  → ATC approves
    @PostMapping("/{flightId}/approve")
    public String approveFlight(@PathVariable String flightId) {
        flightService.approveFlight(flightId);
        return "redirect:/flights";
    }

    // POST /flights/{id}/reject  → ATC rejects
    @PostMapping("/{flightId}/reject")
    public String rejectFlight(@PathVariable String flightId,
                              @RequestParam String reason) {
        flightService.rejectFlight(flightId, reason);
        return "redirect:/flights";
    }

    // POST /flights/{id}/status  → update status
    @PostMapping("/{flightId}/status")
    public String updateStatus(@PathVariable String flightId,
                              @RequestParam FlightStatus status) {
        flightService.updateFlight(flightId, status);
        return "redirect:/flights";
    }

    // GET /flights/monitor  → ATC monitor view
    @GetMapping("/monitor")
    public String monitorAircraft(Model model) {
        model.addAttribute("activeFlights",
            flightService.getFlightsByStatus(FlightStatus.IN_PROGRESS));
        model.addAttribute("pendingFlights",
            flightService.getFlightsByStatus(FlightStatus.PENDING));
        return "flight/monitor";
    }
}


