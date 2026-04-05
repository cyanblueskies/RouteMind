package com.routemind.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.routemind.service.SavedRouteService;
import com.routemind.dto.SaveRouteRequest;
import com.routemind.dto.SaveRouteResponse;


@RestController
@RequestMapping("/api/saved-routes")
public class SavedRouteController {
    // API endpoints for managing saved routes.
    // /GET /saved-routes - Get all saved routes for the authenticated user.
    // /POST /saved-routes - Save a new route for the authenticated user.
    // /DELETE /saved-routes/{routeId} - Delete a saved route by ID for the authenticated user.
    // /GET /saved-routes/search?query={query} - Search saved routes by name.

    @Autowired
    private SavedRouteService savedRouteService;

    @GetMapping("/")
    public List<SaveRouteResponse> getRoutes(
            @CookieValue(value = "user_id", required = true) Long userId
    ) {
        return savedRouteService.getSavedRoutesByUserId(userId);
    }

    @PostMapping("/")
    public SaveRouteResponse saveRoute(
            @CookieValue(value = "user_id", required = true) Long userId,
            @RequestBody SaveRouteRequest request
    ) {
        return savedRouteService.saveRoute(userId, request);
    }

    @DeleteMapping("/{routeId}")
    public void deleteRoute(
            @CookieValue(value = "user_id", required = true) Long userId,
            @PathVariable Long routeId
    ) {
        savedRouteService.deleteRoute(userId, routeId);
    }

    @GetMapping("/search")
    public List<SaveRouteResponse> searchRoutes(
            @CookieValue(value = "user_id", required = true) Long userId,
            @RequestParam String query
    ) {
        return savedRouteService.searchRoutes(userId, query);
    }
}
