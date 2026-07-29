package mk.edu.seeu.logistics_tracker.repository;

import mk.edu.seeu.logistics_tracker.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByOriginId(Long originId);
}