package mk.edu.seeu.logistics_tracker.service;

import mk.edu.seeu.logistics_tracker.dto.RouteResult;
import mk.edu.seeu.logistics_tracker.entity.*;
import mk.edu.seeu.logistics_tracker.repository.ShipmentEventRepository;
import mk.edu.seeu.logistics_tracker.repository.ShipmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Service
public class ShipmentSimulationService {

    private final ShipmentRepository shipmentRepository;
    private final ShipmentEventRepository shipmentEventRepository;
    private final ExecutorService virtualThreadExecutor;

    public ShipmentSimulationService(
            ShipmentRepository shipmentRepository,
            ShipmentEventRepository shipmentEventRepository,
            ExecutorService virtualThreadExecutor) {
        this.shipmentRepository = shipmentRepository;
        this.shipmentEventRepository = shipmentEventRepository;
        this.virtualThreadExecutor = virtualThreadExecutor;
    }

    public void startSimulation(Shipment shipment, RouteResult route, int millisPerLeg) {
        virtualThreadExecutor.submit(() -> runSimulation(shipment, route, millisPerLeg));
    }

    private void runSimulation(Shipment shipment, RouteResult route, int millisPerLeg) {
        List<Warehouse> path = route.getPath();

        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setCurrentLocation(path.get(0));
        shipmentRepository.save(shipment);
        logEvent(shipment, path.get(0), ShipmentStatus.IN_TRANSIT, "Departed origin");

        for (int i = 1; i < path.size(); i++) {
            try {
                Thread.sleep(millisPerLeg);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            Warehouse current = path.get(i);
            shipment.setCurrentLocation(current);

            boolean isFinalStop = (i == path.size() - 1);
            if (isFinalStop) {
                shipment.setStatus(ShipmentStatus.DELIVERED);
                shipment.setActualArrival(LocalDateTime.now());
            }

            shipmentRepository.save(shipment);
            logEvent(shipment, current, shipment.getStatus(),
                    isFinalStop ? "Arrived at destination" : "Arrived at intermediate stop");
        }
    }

    private void logEvent(Shipment shipment, Warehouse warehouse, ShipmentStatus status, String notes) {
        ShipmentEvent event = new ShipmentEvent();
        event.setShipment(shipment);
        event.setWarehouse(warehouse);
        event.setStatusAtEvent(status);
        event.setOccurredAt(LocalDateTime.now());
        event.setNotes(notes);
        shipmentEventRepository.save(event);
    }
}