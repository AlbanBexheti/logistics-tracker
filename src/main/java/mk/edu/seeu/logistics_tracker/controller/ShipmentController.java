package mk.edu.seeu.logistics_tracker.controller;

import mk.edu.seeu.logistics_tracker.dto.RouteResult;
import mk.edu.seeu.logistics_tracker.dto.StartShipmentRequest;
import mk.edu.seeu.logistics_tracker.entity.Shipment;
import mk.edu.seeu.logistics_tracker.repository.ShipmentRepository;
import mk.edu.seeu.logistics_tracker.service.RouteOptimizationService;
import mk.edu.seeu.logistics_tracker.service.ShipmentSimulationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/shipments")
public class ShipmentController {

    private final ShipmentRepository shipmentRepository;
    private final RouteOptimizationService routeOptimizationService;
    private final ShipmentSimulationService shipmentSimulationService;

    public ShipmentController(
            ShipmentRepository shipmentRepository,
            RouteOptimizationService routeOptimizationService,
            ShipmentSimulationService shipmentSimulationService) {
        this.shipmentRepository = shipmentRepository;
        this.routeOptimizationService = routeOptimizationService;
        this.shipmentSimulationService = shipmentSimulationService;
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<String> startShipment(@PathVariable Long id, @RequestBody StartShipmentRequest request) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Shipment not found: " + id));

        RouteResult route = routeOptimizationService.findShortestPath(
                shipment.getOrigin(),
                shipment.getDestination(),
                request.getWeightType()
        );

        int millisPerLeg = request.getMillisPerLeg() != null ? request.getMillisPerLeg() : 2000;
        shipmentSimulationService.startSimulation(shipment, route, millisPerLeg);

        return ResponseEntity.ok("Simulation started for shipment " + id + " via " + route.getPath().size() + " stops");
    }
}