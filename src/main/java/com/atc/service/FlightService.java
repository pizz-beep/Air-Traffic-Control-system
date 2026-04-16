package com.atc.service;

import com.atc.model.Flight;
import com.atc.model.Flight.FlightStatus;
import com.atc.model.Pilot;
import com.atc.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    // Create / Submit Flight Plan
    @Transactional
    public Flight createFlight(Flight flight) {
        flight.setStatus(FlightStatus.PENDING);
        return flightRepository.save(flight);
    }

    // Approve Flight Plan (ATC)
    @Transactional
    public Flight approveFlight(String flightId) {
        Flight flight = findByFlightId(flightId);
        flight.setStatus(FlightStatus.APPROVED);
        return flightRepository.save(flight);
    }

    // Reject Flight Plan (ATC)
    @Transactional
    public Flight rejectFlight(String flightId, String reason) {
        Flight flight = findByFlightId(flightId);
        flight.setStatus(FlightStatus.REJECTED);
        flight.setRemarks(reason);
        return flightRepository.save(flight);
    }

    // Update Flight Status
    @Transactional
    public Flight updateFlight(String flightId, FlightStatus status) {
        Flight flight = findByFlightId(flightId);
        flight.updateStatus(status);
        return flightRepository.save(flight);
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public List<Flight> getFlightsByStatus(FlightStatus status) {
        return flightRepository.findByStatus(status);
    }

    public List<Flight> getFlightsByPilot(Long pilotId) {
        return flightRepository.findByPilotUserId(pilotId);
    }

    public Flight findByFlightId(String flightId) {
        return flightRepository.findByFlightId(flightId)
            .orElseThrow(() -> new RuntimeException(
                "Flight not found: " + flightId));
    }
}


