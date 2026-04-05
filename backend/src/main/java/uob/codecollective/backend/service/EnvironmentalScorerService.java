package uob.codecollective.backend.service;

import org.springframework.stereotype.Service;
import uob.codecollective.backend.dto.EnvironmentalScores;
import uob.codecollective.backend.dto.RouteSegment;
import java.util.List;

//Calculates environmental comfort scores for a route based on OSM road properties.Scores are distance-weighted averages across all segments.
@Service
public class EnvironmentalScorerService {

    public EnvironmentalScores scoreRoute(List<RouteSegment> segments) {
        if (segments == null || segments.isEmpty()) {
            return new EnvironmentalScores(50, 50, 50, 50);
        }

        double totalDistance = segments.stream()
                .mapToDouble(RouteSegment::getDistanceMeters)
                .sum();

        // edge case: avoid division by zero when all segments have zero distance
        if (totalDistance == 0) {
            return new EnvironmentalScores(50, 50, 50, 50);
        }

        double noiseScore = 0;
        double lightingScore = 0;
        double pollutionScore = 0;
        double wheelchairScore = 0;

        for (RouteSegment seg : segments) {
            double weight = seg.getDistanceMeters() / totalDistance;
            noiseScore += scoreNoise(seg.getRoadClass()) * weight;
            lightingScore += scoreLighting(seg.isLit()) * weight;
            pollutionScore += scorePollution(seg.getRoadClass()) * weight;
            wheelchairScore += scoreWheelchair(seg.getSurface(), seg.getSmoothness()) * weight;
        }

        return new EnvironmentalScores(noiseScore, lightingScore, pollutionScore, wheelchairScore);
    }

    /** Maps OSM road class to noise comfort score */
    double scoreNoise(String roadClass) {
        if (roadClass == null) return 60;
        return switch (roadClass.toUpperCase()) {
            case "MOTORWAY", "TRUNK" -> 10;
            case "PRIMARY" -> 25;
            case "SECONDARY" -> 40;
            case "TERTIARY" -> 65;
            case "RESIDENTIAL", "LIVING_STREET" -> 75;
            case "FOOTWAY", "PATH", "PEDESTRIAN", "CYCLEWAY" -> 95;
            default -> 60;
        };
    }

    // Lit streets score 100, unlit score 20
    double scoreLighting(boolean lit) {
        return lit ? 100 : 30;
    }

    // Maps road class to pollution score, similar to noise but different weights
    double scorePollution(String roadClass) {
        if (roadClass == null) return 60;
        return switch (roadClass.toUpperCase()) {
            case "MOTORWAY", "TRUNK" -> 10;
            case "PRIMARY" -> 30;
            case "SECONDARY" -> 50;
            case "TERTIARY" -> 65;
            case "RESIDENTIAL", "LIVING_STREET" -> 75;
            case "FOOTWAY", "PATH", "PEDESTRIAN", "CYCLEWAY" -> 95;
            default -> 60;
        };
    }

    //Combines surface type and smoothness for wheelchair accessibility
    double scoreWheelchair(String surface, String smoothness) {
        double surfaceScore = scoreSurface(surface);
        double smoothnessScore = scoreSmoothness(smoothness);
        return (surfaceScore + smoothnessScore) / 2.0;
    }

    private double scoreSurface(String surface) {
        if (surface == null) return 50;
        return switch (surface.toUpperCase()) {
            case "PAVED", "ASPHALT", "CONCRETE" -> 95;
            case "PAVING_STONES"  -> 80;
            case "SETT" -> 65;
            case "COMPACTED", "FINE_GRAVEL" -> 60;
            case "GRAVEL", "DIRT", "GRASS", "SAND" -> 20;
            default -> 50;
        };
    }

    private double scoreSmoothness(String smoothness) {
        if (smoothness == null) return 50;
        return switch (smoothness.toUpperCase()) {
            case "EXCELLENT" -> 100;
            case "GOOD" -> 80;
            case "INTERMEDIATE" -> 50;
            case "BAD" -> 30;
            case "VERY_BAD", "HORRIBLE" -> 10;
            default -> 50;
        };
    }
}
