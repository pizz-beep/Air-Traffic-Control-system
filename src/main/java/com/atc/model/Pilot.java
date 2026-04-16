package com.atc.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "pilots")
@Data @NoArgsConstructor @AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Pilot extends User {

    private String licenseNumber;

    @OneToMany(mappedBy = "pilot", cascade = CascadeType.ALL)
    private List<Flight> flights;

    @Override
    public void login() {
        System.out.println("Pilot " + getName() + " logged in.");
    }

    @Override
    public void logout() {
        System.out.println("Pilot " + getName() + " logged out.");
    }

    public void submitFlightPlan(Flight flight) {
        flight.setStatus(Flight.FlightStatus.PENDING);
        System.out.println("Flight plan submitted: " + flight.getFlightId());
    }
}


