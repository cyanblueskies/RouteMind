package uob.codecollective.backend.dto;

import java.util.List;

public class ScoredRoute {

    public List<Point> points;

    public double totalScore;
    public double noiseScore;
    public double pollutionScore;
    public double lightingScore;
    public double wheelchairScore;

    public ScoredRoute(List<Point> points, double totalScore, double noiseScore, double pollutionScore, double lightingScore, double wheelchairScore) {
        this.points = points;
        this.totalScore = totalScore;
        this.noiseScore = noiseScore;
        this.pollutionScore = pollutionScore;
        this.lightingScore = lightingScore;
        this.wheelchairScore = wheelchairScore;
    }

    public List<Point> getPoints() {
        return points;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public double getNoiseScore() {
        return noiseScore;
    }

    public double getPollutionScore() {
        return pollutionScore;
    }

    public double getLightingScore() {
        return lightingScore;
    }

    public double getWheelchairScore() {
        return wheelchairScore;
    }

    @Override
    public String toString() {
        return "ScoredRoute{" +
                "points=" + points +
                ", totalScore=" + totalScore +
                ", noiseScore=" + noiseScore +
                ", pollutionScore=" + pollutionScore +
                ", lightingScore=" + lightingScore +
                ", wheelchairScore=" + wheelchairScore +
                '}';
    }

}
