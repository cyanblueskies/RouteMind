package com.routemind.dto;
public class ScoredRouteRequest {

    public Point start;
    public Point dest;

    public int noiseWeight;
    public int pollutionWeight;
    public int lightingWeight;
    public int wheelchairWeight;

    public ScoredRouteRequest() {}

    public ScoredRouteRequest(Point start, Point dest,
                              int noiseWeight, int pollutionWeight,
                              int lightingWeight, int wheelchairWeight) {
        this.start = start;
        this.dest = dest;
        this.noiseWeight = noiseWeight;
        this.pollutionWeight = pollutionWeight;
        this.lightingWeight = lightingWeight;
        this.wheelchairWeight = wheelchairWeight;
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

    public Point getStart() {
        return start;
    }

    public Point getDest() {
        return dest;
    }

}
