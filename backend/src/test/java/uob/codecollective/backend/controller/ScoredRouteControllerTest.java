package uob.codecollective.backend.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import uob.codecollective.backend.dto.*;
import uob.codecollective.backend.service.EnvironmentalScorerService;
import uob.codecollective.backend.service.RouteScorerService;
import uob.codecollective.backend.service.RouterService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ScoredRouteControllerTest {

    private RouterService routerService;
    private ScoredRouteController controller;

    @BeforeEach
    void setUp() {
        routerService = mock(RouterService.class);
        RouteScorerService routeScorerService = mock(RouteScorerService.class);
        controller = new ScoredRouteController(routerService, routeScorerService);
    }

    @Test
    void returnsOkWithRoutes() {
        // mock RouterService to return a list of routes
        Route mockRoute = new Route(
                "Route 1",
                List.of(new Point(52.4862, -1.8904), new Point(52.4840, -1.8920)),
                75.0, 80.0, 70.0, 90.0, 60.0, 1.2, 15.0
        );
        when(routerService.create(any())).thenReturn(List.of(mockRoute));

        ScoredRouteRequest request = new ScoredRouteRequest(
                new Point(52.4862, -1.8904),
                new Point(52.4800, -1.9000),
                60, 50, 40, 30
        );

        ResponseEntity<List<Route>> response = controller.getScoredRoute(request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("Route 1", response.getBody().get(0).name);
        assertTrue(response.getBody().get(0).totalScore >= 0);
    }

    @Test
    void rejectsNullStart() {
        ScoredRouteRequest request = new ScoredRouteRequest(
                null, new Point(52.48, -1.90), 50, 50, 50, 50
        );
        ResponseEntity<List<Route>> response = controller.getScoredRoute(request);
        assertEquals(400, response.getStatusCode().value());
    }

    @Test
    void rejectsNullDest() {
        ScoredRouteRequest request = new ScoredRouteRequest(
                new Point(52.48, -1.90), null, 50, 50, 50, 50
        );
        ResponseEntity<List<Route>> response = controller.getScoredRoute(request);
        assertEquals(400, response.getStatusCode().value());
    }
}
