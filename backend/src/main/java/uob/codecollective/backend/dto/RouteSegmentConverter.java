package uob.codecollective.backend.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.graphhopper.util.details.PathDetail;

public class RouteSegmentConverter {

    private static final double EARTH_RADIUS_METERS = 6_371_000;

    private static final Set<String> LIKELY_LIT = Set.of(
        "RESIDENTIAL", "LIVING_STREET", "TERTIARY", "SECONDARY",
        "PRIMARY", "TRUNK", "MOTORWAY", "PEDESTRIAN"
    );

    /**
     * Creates route segments using real OSM data from GraphHopper path details.
     */
    public static List<RouteSegment> fromPathDetails(
            List<Point> points,
            Map<String, List<PathDetail>> pathDetails
    ) {
        List<RouteSegment> segments = new ArrayList<>();
        if (points == null || points.size() < 2) return segments;

        List<PathDetail> roadClassDetails = pathDetails.getOrDefault("road_class", List.of());
        List<PathDetail> surfaceDetails = pathDetails.getOrDefault("surface", List.of());
        List<PathDetail> smoothnessDetails = pathDetails.getOrDefault("smoothness", List.of());

        for (int i = 0; i < points.size() - 1; i++) {
            Point from = points.get(i);
            Point to = points.get(i + 1);
            double distance = haversine(from.lat(), from.lon(), to.lat(), to.lon());

            String roadClass = findDetail(roadClassDetails, i, "RESIDENTIAL");
            String surface = findDetail(surfaceDetails, i, "ASPHALT");
            String smoothness = findDetail(smoothnessDetails, i, "GOOD");
            boolean lit = LIKELY_LIT.contains(roadClass.toUpperCase());

            segments.add(new RouteSegment(roadClass.toUpperCase(), surface.toUpperCase(),
                    smoothness.toUpperCase(), lit, distance));
        }
        return segments;
    }

    private static String findDetail(List<PathDetail> details, int pointIndex, String defaultValue) {
        for (PathDetail detail : details) {
            if (pointIndex >= detail.getFirst() && pointIndex < detail.getLast()) {
                Object val = detail.getValue();
                if (val != null) return val.toString();
            }
        }
        return defaultValue;
    }

    /**
     * Fallback: creates segments with default values (used when router is disabled).
     */
    public static List<RouteSegment> fromPoints(List<Point> points) {
        List<RouteSegment> segments = new ArrayList<>();
        if (points == null || points.size() < 2) return segments;

        for (int i = 0; i < points.size() - 1; i++) {
            Point from = points.get(i);
            Point to = points.get(i + 1);
            double distance = haversine(from.lat(), from.lon(), to.lat(), to.lon());
            segments.add(new RouteSegment("RESIDENTIAL", "ASPHALT", "GOOD", true, distance));
        }
        return segments;
    }

    static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                 * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_METERS * c;
    }
}
