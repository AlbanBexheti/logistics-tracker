package mk.edu.seeu.logistics_tracker;

import mk.edu.seeu.logistics_tracker.dto.RouteResult;
import mk.edu.seeu.logistics_tracker.dto.RouteWeightType;
import mk.edu.seeu.logistics_tracker.entity.Route;
import mk.edu.seeu.logistics_tracker.entity.Warehouse;
import mk.edu.seeu.logistics_tracker.repository.RouteRepository;
import mk.edu.seeu.logistics_tracker.service.RouteOptimizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RouteOptimizationServiceTest {

    private RouteRepository routeRepository;
    private RouteOptimizationService service;

    private Warehouse a, b, c, d;

    @BeforeEach
    void setUp() {
        routeRepository = mock(RouteRepository.class);
        service = new RouteOptimizationService(routeRepository);

        a = new Warehouse(1L, "A", 0.0, 0.0, 100);
        b = new Warehouse(2L, "B", 0.0, 0.0, 100);
        c = new Warehouse(3L, "C", 0.0, 0.0, 100);
        d = new Warehouse(4L, "D", 0.0, 0.0, 100);
    }

    @Nested
    class ShortestPathByDistance {

        @Test
        void picksCheaperPathOverDirectLongerPath() {
            List<Route> routes = List.of(
                    route(a, b, 5.0, 10, 5.0),
                    route(b, d, 3.0, 10, 3.0),
                    route(a, c, 2.0, 10, 2.0),
                    route(c, d, 2.0, 10, 2.0)
            );
            when(routeRepository.findAll()).thenReturn(routes);

            RouteResult result = service.findShortestPath(a, d, RouteWeightType.DISTANCE);

            assertEquals(4.0, result.getTotalWeight(), 0.001);
            assertEquals(List.of(a, c, d), result.getPath());
        }

        @Test
        void directRouteIsUsedWhenItsCheapest() {
            List<Route> routes = List.of(
                    route(a, d, 1.0, 5, 1.0),
                    route(a, b, 5.0, 10, 5.0),
                    route(b, d, 5.0, 10, 5.0)
            );
            when(routeRepository.findAll()).thenReturn(routes);

            RouteResult result = service.findShortestPath(a, d, RouteWeightType.DISTANCE);

            assertEquals(1.0, result.getTotalWeight(), 0.001);
            assertEquals(List.of(a, d), result.getPath());
        }
    }

    @Nested
    class WeightTypeSwitching {

        @Test
        void optimizesByTimeInsteadOfDistance() {
            List<Route> routes = List.of(
                    route(a, b, 1.0, 100, 1.0),
                    route(b, d, 1.0, 100, 1.0),
                    route(a, c, 10.0, 5, 10.0),
                    route(c, d, 10.0, 5, 10.0)
            );
            when(routeRepository.findAll()).thenReturn(routes);

            RouteResult result = service.findShortestPath(a, d, RouteWeightType.TIME);

            assertEquals(10.0, result.getTotalWeight(), 0.001);
            assertEquals(List.of(a, c, d), result.getPath());
        }
    }

    @Nested
    class EdgeCases {

        @Test
        void throwsWhenNoPathExists() {
            List<Route> routes = List.of(
                    route(a, b, 5.0, 10, 5.0)
            );
            when(routeRepository.findAll()).thenReturn(routes);

            assertThrows(NoSuchElementException.class,
                    () -> service.findShortestPath(a, d, RouteWeightType.DISTANCE));
        }

        @Test
        void sameOriginAndDestinationReturnsZeroWeight() {
            when(routeRepository.findAll()).thenReturn(List.of());

            RouteResult result = service.findShortestPath(a, a, RouteWeightType.DISTANCE);

            assertEquals(0.0, result.getTotalWeight(), 0.001);
            assertEquals(List.of(a), result.getPath());
        }
    }

    private Route route(Warehouse origin, Warehouse destination, double distanceKm, int transitMinutes, double cost) {
        return new Route(null, origin, destination, distanceKm, transitMinutes, cost);
    }
}