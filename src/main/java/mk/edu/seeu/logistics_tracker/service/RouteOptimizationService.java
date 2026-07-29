package mk.edu.seeu.logistics_tracker.service;

import mk.edu.seeu.logistics_tracker.dto.RouteResult;
import mk.edu.seeu.logistics_tracker.dto.RouteWeightType;
import mk.edu.seeu.logistics_tracker.entity.Route;
import mk.edu.seeu.logistics_tracker.entity.Warehouse;
import mk.edu.seeu.logistics_tracker.repository.RouteRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RouteOptimizationService {

    private final RouteRepository routeRepository;

    public RouteOptimizationService(RouteRepository routeRepository) {
        this.routeRepository = routeRepository;
    }

    public RouteResult findShortestPath(Warehouse origin, Warehouse destination, RouteWeightType weightType) {
        List<Route> allRoutes = routeRepository.findAll();

        Map<Long, List<Route>> adjacency = new HashMap<>();
        for (Route route : allRoutes) {
            adjacency
                    .computeIfAbsent(route.getOrigin().getId(), k -> new ArrayList<>())
                    .add(route);
        }

        Map<Long, Double> distances = new HashMap<>();
        Map<Long, Long> previous = new HashMap<>();
        PriorityQueue<Long> queue = new PriorityQueue<>(Comparator.comparingDouble(id -> distances.getOrDefault(id, Double.MAX_VALUE)));

        distances.put(origin.getId(), 0.0);
        queue.add(origin.getId());

        Map<Long, Warehouse> warehouseLookup = new HashMap<>();
        warehouseLookup.put(origin.getId(), origin);
        warehouseLookup.put(destination.getId(), destination);

        while (!queue.isEmpty()) {
            Long currentId = queue.poll();

            if (currentId.equals(destination.getId())) {
                break;
            }

            List<Route> outgoing = adjacency.getOrDefault(currentId, Collections.emptyList());
            for (Route route : outgoing) {
                double weight = extractWeight(route, weightType);
                double newDist = distances.get(currentId) + weight;
                Long neighborId = route.getDestination().getId();
                warehouseLookup.put(neighborId, route.getDestination());

                if (newDist < distances.getOrDefault(neighborId, Double.MAX_VALUE)) {
                    distances.put(neighborId, newDist);
                    previous.put(neighborId, currentId);
                    queue.add(neighborId);
                }
            }
        }

        if (!distances.containsKey(destination.getId())) {
            throw new NoSuchElementException("No path found between the given warehouses");
        }

        List<Warehouse> path = new ArrayList<>();
        Long step = destination.getId();
        while (step != null) {
            path.add(0, warehouseLookup.get(step));
            step = previous.get(step);
        }

        return new RouteResult(path, distances.get(destination.getId()));
    }

    private double extractWeight(Route route, RouteWeightType weightType) {
        return switch (weightType) {
            case DISTANCE -> route.getDistanceKm();
            case TIME -> route.getAvgTransitMinutes();
            case COST -> route.getCost();
        };
    }
}