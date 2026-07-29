package mk.edu.seeu.logistics_tracker.repository;

import mk.edu.seeu.logistics_tracker.entity.ShipmentEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentEventRepository extends JpaRepository<ShipmentEvent, Long> {
    List<ShipmentEvent> findByShipmentIdOrderByOccurredAtAsc(Long shipmentId);
}