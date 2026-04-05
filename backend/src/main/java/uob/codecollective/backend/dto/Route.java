package uob.codecollective.backend.dto;

import java.util.List;

public class Route {
    public String name;
    public List<Point> points;
    public double totalScore, noiseScore, pollutionScore, lightingScore, wheelchairScore, distanceKm, durationMin;

    public Route() {}

    public Route(String name, List<Point> points) {
        this.name = name;
        this.points = points;
    }

    public Route(String name, List<Point> points,
        double totalScore, double noiseScore,
        double pollutionScore, double lightingScore, double wheelchairScore) {
        this.name = name;
        this.points = points;
        this.totalScore = totalScore;
        this.noiseScore = noiseScore;
        this.pollutionScore = pollutionScore;
        this.lightingScore = lightingScore;
        this.wheelchairScore = wheelchairScore;
    }

    public Route(String name, List<Point> points,
        double totalScore, double noiseScore,
        double pollutionScore, double lightingScore, double wheelchairScore,
        double distanceKm, double durationMin
    ) {
        this.name = name;
        this.points = points;
        this.totalScore = totalScore;
        this.noiseScore = noiseScore;
        this.pollutionScore = pollutionScore;
        this.lightingScore = lightingScore;
        this.wheelchairScore = wheelchairScore;
        this.distanceKm = distanceKm;
        this.durationMin = durationMin;
    }

    public double totalScore() {
        return totalScore;
    }

    public double distanceKm() {
        return distanceKm;
    }

    public double durationMin() {
        return durationMin;
    }

}
