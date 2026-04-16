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
            .orElseThrow(() -> new RuntimeException(
                "Runway not found: " + runwayId));

        Flight flight = flightRepository.findByFlightId(flightId)
            .orElseThrow(() -> new RuntimeException(
                "Flight not found: " + flightId));

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
            .orElseThrow(() -> new RuntimeException(
                "Runway not found: " + runwayId));
        runway.releaseRunway();
        return runwayRepository.save(runway);
    }

    public List<Runway> getAvailableRunways() {
        return runwayRepository.findByStatus(RunwayStatus.AVAILABLE);
    }

    public List<Runway> getAllRunways() {
        return runwayRepository.findAll();
    }
}


