package com.atc.service;

import com.atc.model.Flight;
import com.atc.model.Runway;
import com.atc.model.Runway.RunwayStatus;
import com.atc.repository.FlightRepository;
import com.atc.repository.RunwayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RunwayService {

    private final RunwayRepository runwayRepository;
    private final FlightRepository flightRepository;

    // Allocate an available runway to a flight
    @Transactional
    public Runway allocateRunway(String runwayId, String flightId) {
        Runway runway = runwayRepository.findByRunwayId(runwayId)
            .orElseThrow(() -> new RuntimeException("Runway not found: " + runwayId));

        Flight flight = flightRepository.findByFlightId(flightId)
            .orElseThrow(() -> new RuntimeException("Flight not found: " + flightId));

        runway.assignRunway();
        flight.setRunway(runway);

        runwayRepository.save(runway);
        flightRepository.save(flight);
        return runway;
    }

    // Release runway after landing
    @Transactional
    public Runway releaseRunway(String runwayId) {
        Runway runway = runwayRepository.findByRunwayId(runwayId)
            .orElseThrow(() -> new RuntimeException("Runway not found: " + runwayId));
        runway.releaseRunway();
        return runwayRepository.save(runway);
    }

    // Create a new runway (Admin)
    @Transactional
    public Runway createRunway(String runwayId, String direction, Integer lengthMeters) {
        if (runwayRepository.findByRunwayId(runwayId).isPresent()) {
            throw new RuntimeException("Runway ID already exists: " + runwayId);
        }
        Runway runway = new Runway();
        runway.setRunwayId(runwayId);
        runway.setDirection(direction);
        runway.setLengthMeters(lengthMeters);
        runway.setStatus(RunwayStatus.AVAILABLE);
        return runwayRepository.save(runway);
    }

    // Update runway status (Admin)
    @Transactional
    public Runway setRunwayStatus(String runwayId, RunwayStatus status) {
        Runway runway = runwayRepository.findByRunwayId(runwayId)
            .orElseThrow(() -> new RuntimeException("Runway not found: " + runwayId));
        runway.setStatus(status);
        return runwayRepository.save(runway);
    }

    // Delete runway (Admin)
    @Transactional
    public void deleteRunway(String runwayId) {
        Runway runway = runwayRepository.findByRunwayId(runwayId)
            .orElseThrow(() -> new RuntimeException("Runway not found: " + runwayId));
        runwayRepository.delete(runway);
    }

    public List<Runway> getAvailableRunways() {
        return runwayRepository.findByStatus(RunwayStatus.AVAILABLE);
    }

    public List<Runway> getAllRunways() {
        return runwayRepository.findAll();
    }

    public long countByStatus(RunwayStatus status) {
        return runwayRepository.findByStatus(status).size();
    }
}
