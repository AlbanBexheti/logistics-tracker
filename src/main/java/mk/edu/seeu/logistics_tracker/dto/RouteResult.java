package mk.edu.seeu.logistics_tracker.dto;

import mk.edu.seeu.logistics_tracker.entity.Warehouse;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
public class RouteResult {
    private List<Warehouse> path;
    private double totalWeight;
}