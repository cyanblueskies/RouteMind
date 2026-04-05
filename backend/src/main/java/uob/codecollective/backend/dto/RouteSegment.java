package uob.codecollective.backend.dto;

//Represents a single segment of a walking route with its OSM properties
public class RouteSegment {

    private String roadClass;
    private String surface;
    private String smoothness; //needed for wheelchair scoring
    private boolean lit;
    private double distanceMeters;

    public RouteSegment() {}

    public RouteSegment(String roadClass, String surface, String smoothness,
                        boolean lit, double distanceMeters) {
        this.roadClass = roadClass;
        this.surface = surface;
        this.smoothness = smoothness;
        this.lit = lit;
        this.distanceMeters = Math.max(0,distanceMeters);
    }

    public String getRoadClass() { return roadClass; }
    public void setRoadClass(String roadClass) { this.roadClass = roadClass; }

    public String getSurface() { return surface; }
    public void setSurface(String surface) { this.surface = surface; }

    public String getSmoothness() { return smoothness; }
    public void setSmoothness(String smoothness) { this.smoothness = smoothness; }

    public boolean isLit() { return lit; }
    public void setLit(boolean lit) { this.lit = lit; }

    public double getDistanceMeters() { return distanceMeters; }
    public void setDistanceMeters(double distanceMeters) { this.distanceMeters = distanceMeters; }

    @Override
    public String toString() {
        return "RouteSegment{" +
                "roadClass='" + roadClass + '\'' +
                ", surface='" + surface + '\'' +
                ", smoothness='" + smoothness + '\'' +
                ", lit=" + lit +
                ", distanceMeters=" + distanceMeters +
                '}';
    }

}
