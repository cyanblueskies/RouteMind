package uob.codecollective.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uob.codecollective.backend.dto.*;
import uob.codecollective.backend.service.RouteScorerService;

//REST endpoint for scoring and comparing routes based on environmental factors.
@RestController
@RequestMapping("/api/scoring")
public class ScoringController {

    private final RouteScorerService routeScorerService;

    public ScoringController(RouteScorerService routeScorerService) {
        this.routeScorerService = routeScorerService;
    }

    @PostMapping
    public ResponseEntity<ScoredRoute> scoreRoute(@RequestBody ScoringRequest request) {
        if (request.getPoints() == null || request.getPoints().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        int nw = Math.max(0, request.getNoiseWeight());
        int lw = Math.max(0, request.getLightingWeight());
        int aw = Math.max(0, request.getPollutionWeight());
        int ww = Math.max(0, request.getWheelchairWeight());

        RouteRequest routeRequest = new RouteRequest();
        routeRequest.start = request.getPoints().get(0);
        routeRequest.dest = request.getPoints().get(request.getPoints().size() - 1);
        routeRequest.noiseWeight = nw;
        routeRequest.lightingWeight = lw;
        routeRequest.pollutionWeight = aw;
        routeRequest.wheelchairWeight = ww;

        ScoredRoute scoredRoute = routeScorerService.scoreRoute(request.getPoints(), routeRequest);
        return ResponseEntity.ok(scoredRoute);
    }
}
