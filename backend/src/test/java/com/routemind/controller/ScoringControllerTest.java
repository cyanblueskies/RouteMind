package com.routemind.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import com.routemind.dto.Point;
import com.routemind.dto.ScoringRequest;
import com.routemind.dto.ScoredRoute;
import com.routemind.service.RouteScorerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScoringControllerTest {

    private ScoringController controller;

    @BeforeEach
    void setUp() {
        controller = new ScoringController(new RouteScorerService());
    }

    // valid request should return 200 with scores
    @Test
    void testScoreRouteReturns200() {
        ScoringRequest request = new ScoringRequest();
        request.setPoints(List.of(new Point(52.48, -1.89), new Point(52.49, -1.90)));
        request.setNoiseWeight(50);
        request.setLightingWeight(50);
        request.setPollutionWeight(50);
        request.setWheelchairWeight(50);

        ResponseEntity<ScoredRoute> response = controller.scoreRoute(request);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getTotalScore() >= 0.0 && response.getBody().getTotalScore() <= 100.0);
        assertTrue(response.getBody().getNoiseScore() >= 0.0);
        assertTrue(response.getBody().getPollutionScore() >= 0.0);
        assertTrue(response.getBody().getLightingScore() >= 0.0);
        assertTrue(response.getBody().getWheelchairScore() >= 0.0);

    }

    // empty points list should return 400
    @Test
    void testEmptyPointsReturns400() {
        ScoringRequest request = new ScoringRequest();
        request.setPoints(List.of());
        request.setNoiseWeight(50);
        request.setLightingWeight(50);
        request.setPollutionWeight(50);
        request.setWheelchairWeight(50);
        ResponseEntity<ScoredRoute> response = controller.scoreRoute(request);
        assertEquals(400, response.getStatusCode().value());
    }

    // null points should return 400
    @Test
    void testNullPointsReturns400() {
        ScoringRequest request = new ScoringRequest();
        request.setPoints(null);
        request.setNoiseWeight(50);
        request.setLightingWeight(50);
        request.setPollutionWeight(50);
        request.setWheelchairWeight(50);
        ResponseEntity<ScoredRoute> response = controller.scoreRoute(request);
        assertEquals(400, response.getStatusCode().value());
    }
}
