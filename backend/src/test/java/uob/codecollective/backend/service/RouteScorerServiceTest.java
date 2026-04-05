package uob.codecollective.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uob.codecollective.backend.dto.Point;
import uob.codecollective.backend.dto.RouteRequest;
import uob.codecollective.backend.dto.ScoredRoute;

import java.util.List;

class RouteScorerServiceTest {

    private RouteScorerService scorer;

    @BeforeEach
    void setUp() {
        scorer = new RouteScorerService();
    }

    private List<Point> samplePoints() {
        return List.of(
            new Point(51.5074, -0.1278),
            new Point(51.5074, -0.1278)
        );
    }

    @Test
    void testScoreRouteReturnsNonNull() {
        RouteRequest request = new RouteRequest(samplePoints().get(0), samplePoints().get(1),
            50, 50, 50, 50);
        ScoredRoute res = scorer.scoreRoute(samplePoints(), request);
        assertNotNull(res);
        assertNotNull(res.getPoints());
        assertEquals(2, res.getPoints().size());
    }

    @Test
    void testAllWeightsZero() {
        RouteRequest request = new RouteRequest(samplePoints().get(0), samplePoints().get(1),
            0, 0, 0, 0);
        ScoredRoute res = scorer.scoreRoute(samplePoints(), request);
        double expected = (res.getNoiseScore() + res.getPollutionScore() + res.getLightingScore() + res.getWheelchairScore()) / 4.0;
        assertEquals(expected, res.getTotalScore(), 0.1);
    }

    @Test
    void testTotalScoreWithinValidRange() {
        RouteRequest request = new RouteRequest(samplePoints().get(0), samplePoints().get(1),
            100, 100, 100, 100);
        ScoredRoute res = scorer.scoreRoute(samplePoints(), request);
        assertTrue(res.getTotalScore() >= 0.0);
        assertTrue(res.getTotalScore() <= 100.0 || res.getTotalScore() <= 1.0);
    }

    @Test
    void testDifferentWeights() {
        RouteRequest request = new RouteRequest(samplePoints().get(0), samplePoints().get(1),
            100, 75, 25, 0);
        ScoredRoute res = scorer.scoreRoute(samplePoints(), request);
        assertNotNull(res);
        assertTrue(res.getTotalScore() >= 0.0);
    }

    @Test
    void testEmptyPoints() {
        RouteRequest request = new RouteRequest(samplePoints().get(0), samplePoints().get(1),
            50, 50, 50, 50);
        ScoredRoute res = scorer.scoreRoute(List.of(), request);
        assertNotNull(res);
    }
}
