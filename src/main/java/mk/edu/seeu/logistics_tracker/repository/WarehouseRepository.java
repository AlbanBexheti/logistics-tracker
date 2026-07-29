package mk.edu.seeu.logistics_tracker.repository;

import mk.edu.seeu.logistics_tracker.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
}