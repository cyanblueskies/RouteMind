package com.routemind.dto;

public class SaveRouteRequest {
    private String routeName;
    private Point start;
    private Point dest;

    private int noiseWeight;
    private int pollutionWeight;
    private int lightingWeight;
    private int wheelchairWeight;

    public SaveRouteRequest() {}

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public Point getStart() { return start; }
    public void setStart(Point start) { this.start = start; }

    public Point getDest() { return dest; }
    public void setDest(Point dest) { this.dest = dest; }

    public int getNoiseWeight() { return noiseWeight; }
    public void setNoiseWeight(int noiseWeight) { this.noiseWeight = noiseWeight; }

    public int getPollutionWeight() { return pollutionWeight; }
    public void setPollutionWeight(int pollutionWeight) { this.pollutionWeight = pollutionWeight; }

    public int getLightingWeight() { return lightingWeight; }
    public void setLightingWeight(int lightingWeight) { this.lightingWeight = lightingWeight; }

    public int getWheelchairWeight() { return wheelchairWeight; }
    public void setWheelchairWeight(int wheelchairWeight) { this.wheelchairWeight = wheelchairWeight; }
}
