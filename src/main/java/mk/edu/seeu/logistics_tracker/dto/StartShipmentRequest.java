package mk.edu.seeu.logistics_tracker.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartShipmentRequest {
    private RouteWeightType weightType;
    private Integer millisPerLeg;
}