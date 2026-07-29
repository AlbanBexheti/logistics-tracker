package mk.edu.seeu.logistics_tracker.repository;

import mk.edu.seeu.logistics_tracker.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}