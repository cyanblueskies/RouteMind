package uob.codecollective.backend.dto;

import com.graphhopper.util.shapes.GHPoint;

public record Point(double lat, double lon) {
    public static Point from(GHPoint point) {
        return new Point(point.lat, point.lon);
    }

    public GHPoint toGHPoint() {
        return new GHPoint(this.lat, this.lon);
    }
}
