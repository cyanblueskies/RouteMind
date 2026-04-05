package uob.codecollective.backend.dto;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RouteSegmentConverterTest {

    // null input should return empty list, not throw
    @Test
    void nullPointsReturnsEmptyList() {
        List<RouteSegment> result = RouteSegmentConverter.fromPoints(null);
        assertTrue(result.isEmpty());
    }

    // single point can't form a segment
    @Test
    void singlePointReturnsEmptyList() {
        List<Point> points = Collections.singletonList(new Point(52.48, -1.89));
        List<RouteSegment> result = RouteSegmentConverter.fromPoints(points);
        assertTrue(result.isEmpty());
    }

    // two points should produce exactly one segment
    @Test
    void twoPointsProduceOneSegment() {
        List<Point> points = Arrays.asList(
            new Point(52.4862, -1.8904),
            new Point(52.4840, -1.8920)
        );

        List<RouteSegment> result = RouteSegmentConverter.fromPoints(points);
        assertEquals(1, result.size());
        assertTrue(result.get(0).getDistanceMeters() > 0);
    }

    // four points should produce three segments
    @Test
    void fourPointsProduceThreeSegments() {
        List<Point> points = Arrays.asList(
            new Point(52.4862, -1.8904),
            new Point(52.4840, -1.8920),
            new Point(52.4820, -1.8950),
            new Point(52.4800, -1.9000)
        );

        List<RouteSegment> result = RouteSegmentConverter.fromPoints(points);
        assertEquals(3, result.size());
    }

    // haversine should give roughly correct distance (Birmingham ~270m between these points)
    @Test
    void haversineDistanceIsReasonable() {
        double distance = RouteSegmentConverter.haversine(
            52.4862, -1.8904,
            52.4840, -1.8920
        );

        // should be roughly 200-300 meters
        assertTrue(distance > 100, "Distance should be > 100m");
        assertTrue(distance < 500, "Distance should be < 500m");
    }

    // same point should give zero distance
    @Test
    void samePointGivesZeroDistance() {
        double distance = RouteSegmentConverter.haversine(
            52.4862, -1.8904,
            52.4862, -1.8904
        );
        assertEquals(0.0, distance, 0.001);
    }

    // segments should have default road properties
    @Test
    void segmentsHaveDefaultProperties() {
        List<Point> points = Arrays.asList(
            new Point(52.4862, -1.8904),
            new Point(52.4840, -1.8920)
        );

        RouteSegment seg = RouteSegmentConverter.fromPoints(points).get(0);
        assertEquals("RESIDENTIAL", seg.getRoadClass());
        assertEquals("ASPHALT", seg.getSurface());
        assertEquals("GOOD", seg.getSmoothness());
        assertTrue(seg.isLit());
    }
}
