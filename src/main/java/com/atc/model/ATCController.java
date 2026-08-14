package com.atc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "atc_controllers")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ATCController extends User {

    private String stationCode;

    @Override
    public void login() {
        System.out.println("ATC Controller " + getName() + " logged in.");
    }

    @Override
    public void logout() {
        System.out.println("ATC Controller " + getName() + " logged out.");
    }

    public void approveFlightPlan(Flight flight) {
        flight.setStatus(Flight.FlightStatus.APPROVED);
    }

    public void rejectFlightPlan(Flight flight, String reason) {
        flight.setStatus(Flight.FlightStatus.REJECTED);
        flight.setRemarks(reason);
    }

    public void assignRunway(Flight flight, Runway runway) {
        runway.setStatus(Runway.RunwayStatus.OCCUPIED);
        flight.setRunway(runway);
    }

    public void monitorAircraft(Flight flight) {
        System.out.println("Monitoring: " + flight.getFlightId()
            + " Status: " + flight.getStatus());
    }
}


