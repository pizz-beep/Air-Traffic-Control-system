package com.atc.repository;

import com.atc.model.Flight;
import com.atc.model.Flight.FlightStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    Optional<Flight> findByFlightId(String flightId);

    List<Flight> findByStatus(FlightStatus status);

    List<Flight> findByPilotUserId(Long pilotId);
}


