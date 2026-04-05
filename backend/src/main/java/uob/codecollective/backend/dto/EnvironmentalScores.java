package uob.codecollective.backend.dto;

//Holds environmental comfort scores for a route.
//Each score ranges from 0 (worst) to 100 (best) and is auto-clamped.
public record EnvironmentalScores(
        double noiseScore,
        double lightingScore,
        double pollutionScore,
        double wheelchairScore
) {
    public EnvironmentalScores {
        noiseScore = clamp(noiseScore);
        lightingScore = clamp(lightingScore);
        pollutionScore = clamp(pollutionScore);
        wheelchairScore = clamp(wheelchairScore);
    }

    private static double clamp(double value) {
        return Math.max(0, Math.min(100, value));
    }
    // Returns the simple average of all four scores
    public double averageScore() {
        return (noiseScore + lightingScore + pollutionScore + wheelchairScore) / 4.0;
    }
}
