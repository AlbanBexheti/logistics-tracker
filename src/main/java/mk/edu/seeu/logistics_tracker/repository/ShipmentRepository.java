package mk.edu.seeu.logistics_tracker.repository;

import mk.edu.seeu.logistics_tracker.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
}