package com.routemind.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.routemind.dto.EnvironmentalScores;
import com.routemind.dto.RouteSegment;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class EnvironmentalScorerTest {

    private EnvironmentalScorerService scorer;

    @BeforeEach
    void setUp() {
        scorer = new EnvironmentalScorerService();
    }

    // handle empty segment list input
    @Test
    void testEmptySegmentsReturnsDefaultScores() {
        EnvironmentalScores scores = scorer.scoreRoute(Collections.emptyList());
        assertEquals(50, scores.noiseScore());
        assertEquals(50, scores.lightingScore());
        assertEquals(50, scores.pollutionScore());
        assertEquals(50, scores.wheelchairScore());
    }

    // handle null input gracefully
    @Test
    void testNullSegmentsReturnsDefaultScores() {
        EnvironmentalScores scores = scorer.scoreRoute(null);
        assertEquals(50, scores.noiseScore());
    }

    // verify footway segment scores high on noise, lighting, air
    @Test
    void testFootwayScoresHigh() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("FOOTWAY", "ASPHALT", "GOOD", true, 500)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(95, scores.noiseScore());
        assertEquals(100, scores.lightingScore());
        assertEquals(95, scores.pollutionScore());
    }

    // verify motorway segment produces low environmental scores
    @Test
    void testMotorwayScoresLow() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("MOTORWAY", "ASPHALT", "GOOD", false, 1000)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(10, scores.noiseScore());
        assertEquals(30, scores.lightingScore());
    }

    // check distance-weighted average: 90% footway(95) + 10% motorway(10) = 86.5
    @Test
    void testDistanceWeightedAverage() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("FOOTWAY", "ASPHALT", "GOOD", true, 900),
                new RouteSegment("MOTORWAY", "ASPHALT", "GOOD", false, 100)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(86.5, scores.noiseScore(), 0.1);
    }

    // residential roads should score moderate on noise and air
    @Test
    void testResidentialRoadScores() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("RESIDENTIAL", "PAVED", "GOOD", true, 300)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(75, scores.noiseScore());
        assertEquals(75, scores.pollutionScore());
    }

    // unlit segments should score low on lighting
    @Test
    void testUnlitSegmentScoresLow() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("FOOTWAY", "ASPHALT", "GOOD", false, 200)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(30, scores.lightingScore());
    }

    // gravel + bad smoothness should score low for wheelchair
    @Test
    void testGravelSurfaceScoresLowWheelchair() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("PATH", "GRAVEL", "BAD", true, 400)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(25, scores.wheelchairScore(), 0.1);
    }

    // null road/surface/smoothness properties should return safe defaults
    @Test
    void testNullPropertiesReturnDefaults() {
        List<RouteSegment> segments = List.of(
                new RouteSegment(null, null, null, true, 100)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(60, scores.noiseScore());
        assertEquals(60, scores.pollutionScore());
        assertEquals(50, scores.wheelchairScore());
    }

    // edge case: all segments have zero distance, should not divide by zero
    @Test
    void testZeroDistanceSegmentsReturnsDefault() {
        List<RouteSegment> segments = List.of(
                new RouteSegment("FOOTWAY", "ASPHALT", "GOOD", true, 0),
                new RouteSegment("RESIDENTIAL", "PAVED", "GOOD", false, 0)
        );
        EnvironmentalScores scores = scorer.scoreRoute(segments);
        assertEquals(50, scores.noiseScore());
    }
}
