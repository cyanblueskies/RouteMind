package com.routemind.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.routemind.dto.EnvironmentalScores;
import com.routemind.dto.Point;
import com.routemind.dto.RouteRequest;
import com.routemind.dto.RouteSegment;
import com.routemind.dto.RouteSegmentConverter;
import com.routemind.dto.ScoredRoute;

//Calculates weighted overall score from environmental scores and user preferences.
@Service
public class RouteScorerService {
    // @param scores           the 4 environmental scores
    // @param noiseWeight      user preference weight for noise (0-100)
    // @param lightingWeight   user preference weight for lighting (0-100)
    // @param pollutionWeight user preference weight for air quality (0-100)
    // @param wheelchairWeight user preference weight for wheelchair access (0-100)
    // @return weighted overall score between 0 and 100

    private final EnvironmentalScorerService environmentalScorerService;

    public RouteScorerService() {
        this.environmentalScorerService = new EnvironmentalScorerService();
    }

    public RouteScorerService(EnvironmentalScorerService environmentalScorerService) {
        this.environmentalScorerService = environmentalScorerService;
    }

    public ScoredRoute scoreRoute(List<Point> points, RouteRequest request) {
        List<RouteSegment> segments = RouteSegmentConverter.fromPoints(points);
        EnvironmentalScores scores = environmentalScorerService.scoreRoute(segments);

        double noiseScore = scores.noiseScore();
        double pollutionScore = scores.pollutionScore();
        double lightingScore = scores.lightingScore();
        double wheelchairScore = scores.wheelchairScore();

        double totalScore = calculateTotalScore(noiseScore, pollutionScore, lightingScore, wheelchairScore, request);

        return new ScoredRoute(points, totalScore, noiseScore, pollutionScore, lightingScore, wheelchairScore);
    }

    private double calculateTotalScore(double noiseScore, double pollutionScore, double lightingScore, double wheelchairScore, RouteRequest request) {

        int totalWeight = request.getNoiseWeight() + request.getPollutionWeight() + request.getLightingWeight() + request.getWheelchairWeight();

        // If total weight is 0, return average score
        if (totalWeight == 0) {
            return (noiseScore + pollutionScore + lightingScore + wheelchairScore) / 4.0;
        }

        return (noiseScore * request.getNoiseWeight()
                + pollutionScore * request.getPollutionWeight()
                + lightingScore * request.getLightingWeight()
                + wheelchairScore * request.getWheelchairWeight())

                / (double) totalWeight;
    }
}
