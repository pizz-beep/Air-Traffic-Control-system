package com.atc.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "runways")
@Data @NoArgsConstructor @AllArgsConstructor
public class Runway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String runwayId;

    @Enumerated(EnumType.STRING)
    private RunwayStatus status = RunwayStatus.AVAILABLE;

    private String direction;   // e.g., "09L", "27R"
    private Integer lengthMeters;

    public enum RunwayStatus {
        AVAILABLE, OCCUPIED, MAINTENANCE, CLOSED
    }

    public void assignRunway() {
        if (this.status == RunwayStatus.AVAILABLE) {
            this.status = RunwayStatus.OCCUPIED;
        } else {
            throw new IllegalStateException(
                "Runway " + runwayId + " is not available.");
        }
    }

    public void releaseRunway() {
        this.status = RunwayStatus.AVAILABLE;
    }
}


4. Repository Layer
