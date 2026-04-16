package com.atc.repository;

import com.atc.model.Runway;
import com.atc.model.Runway.RunwayStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RunwayRepository extends JpaRepository<Runway, Long> {

    List<Runway> findByStatus(RunwayStatus status);

    Optional<Runway> findByRunwayId(String runwayId);
}


