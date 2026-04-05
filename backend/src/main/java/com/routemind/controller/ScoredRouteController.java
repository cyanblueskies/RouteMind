package com.routemind.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.routemind.dto.*;
import com.routemind.service.RouteScorerService;
import com.routemind.service.RouterService;

import java.util.List;

@RestController
@RequestMapping("/api/scored-route")
public class ScoredRouteController {

    private final RouterService routerService;

    public ScoredRouteController(RouterService routerService, RouteScorerService routeScorerService) {
        this.routerService = routerService;
    }


    /**
     * POST /api/scored-route
     * Calculates a route between two points and scores it environmentally.
     */
    @PostMapping
    public ResponseEntity<List<Route>> getScoredRoute(@RequestBody ScoredRouteRequest request) {
        if (request.start == null || request.dest == null) {
            return ResponseEntity.badRequest().build();
        }

        int nw = Math.max(0, request.getNoiseWeight());
        int pw = Math.max(0, request.getPollutionWeight());
        int lw = Math.max(0, request.getLightingWeight());
        int ww = Math.max(0, request.getWheelchairWeight());

        RouteRequest routeRequest = new RouteRequest(
            request.start,
            request.dest,
            nw, pw, lw, ww
        );

        List<Route> routes = routerService.create(routeRequest);
        return ResponseEntity.ok(routes);
    }
}
