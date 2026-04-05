package com.routemind.dto;

public class RouteRequest {

    public RouteRequest() {}
    public RouteRequest(Point start, Point dest) {
        this.start = start;
        this.dest = dest;
        this.noiseWeight = 0;
        this.pollutionWeight = 0;
        this.lightingWeight = 0;
        this.wheelchairWeight = 0;
    }

    // Constructor with raw coordinates
    public RouteRequest(double startingLat, double startingLon, double destinationLat, double destinationLon, int noiseWeight, int pollutionWeight, int lightingWeight, int wheelchairWeight) {
        this.start = new Point(startingLat, startingLon);
        this.dest = new Point(destinationLat, destinationLon);
        this.noiseWeight = noiseWeight;
        this.pollutionWeight = pollutionWeight;
        this.lightingWeight = lightingWeight;
        this.wheelchairWeight = wheelchairWeight;
    }

    // Constructor with Point objects
    public RouteRequest(Point start, Point dest, int noiseWeight, int pollutionWeight, int lightingWeight, int wheelchairWeight) {
        this.start = start;
        this.dest = dest;
        this.noiseWeight = noiseWeight;
        this.pollutionWeight = pollutionWeight;
        this.lightingWeight = lightingWeight;
        this.wheelchairWeight = wheelchairWeight;
    }


    public Point start;
    public Point dest;
    public int noiseWeight;
    public int pollutionWeight;
    public int lightingWeight;
    public int wheelchairWeight;

    public Point getStart() {
        return start;
    }
    public Point getDest() {
        return dest;
    }

    public int getNoiseWeight() {
        return noiseWeight;
    }
    public int getPollutionWeight() {
        return pollutionWeight;
    }
    public int getLightingWeight() {
        return lightingWeight;
    }
    public int getWheelchairWeight() {
        return wheelchairWeight;
    }
}
