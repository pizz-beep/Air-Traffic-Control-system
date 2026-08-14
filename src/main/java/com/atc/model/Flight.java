package com.atc.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "flights")
@Data @NoArgsConstructor @AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String flightId;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Enumerated(EnumType.STRING)
    private FlightStatus status = FlightStatus.PENDING;

    private String remarks;

    private LocalDateTime scheduledDeparture;

    private LocalDateTime scheduledArrival;

    @ManyToOne
    @JoinColumn(name = "pilot_id")
    private Pilot pilot;

    @ManyToOne
    @JoinColumn(name = "runway_id")
    private Runway runway;

    public enum FlightStatus {
        PENDING, APPROVED, REJECTED, IN_PROGRESS,
        LANDED, CANCELLED, EMERGENCY
    }

    public void updateStatus(FlightStatus newStatus) {
        this.status = newStatus;
    }
}


