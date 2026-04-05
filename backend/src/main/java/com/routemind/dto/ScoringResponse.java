package com.routemind.dto;

//Response from the scoring API with detailed and overall scores./
public record ScoringResponse(
        EnvironmentalScores scores,
        double overallScore
) {}
