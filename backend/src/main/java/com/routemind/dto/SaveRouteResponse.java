package com.routemind.dto;

import com.routemind.entity.SavedRoute;

public class SaveRouteResponse {

    private Long routeId;
    private String routeName;
    private Point start;
    private Point dest;

    private int noiseWeight;
    private int pollutionWeight;
    private int lightingWeight;
    private int wheelchairWeight;

    public SaveRouteResponse() {}

    public SaveRouteResponse(SavedRoute route) {
        this.routeId = route.getRouteId();
        this.routeName = route.getRouteName();
        this.start = new Point(route.getStartLat(), route.getStartLon());
        this.dest = new Point(route.getDestLat(), route.getDestLon());
        this.noiseWeight = route.getNoiseWeight();
        this.pollutionWeight = route.getPollutionWeight();
        this.lightingWeight = route.getLightingWeight();
        this.wheelchairWeight = route.getWheelchairWeight();
    }

    public SaveRouteResponse(Long routeId, String routeName, Point start, Point dest, int noiseWeight, int pollutionWeight, int lightingWeight, int wheelchairWeight) {
        this.routeId = routeId;
        this.routeName = routeName;
        this.start = start;
        this.dest = dest;
        this.noiseWeight = noiseWeight;
        this.pollutionWeight = pollutionWeight;
        this.lightingWeight = lightingWeight;
        this.wheelchairWeight = wheelchairWeight;
    }

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

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
