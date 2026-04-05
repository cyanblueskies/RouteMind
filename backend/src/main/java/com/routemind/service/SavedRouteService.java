package com.routemind.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.routemind.controller.errors.ForbiddenError;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.entity.SavedRoute;
import com.routemind.entity.User;
import com.routemind.dto.SaveRouteRequest;
import com.routemind.dto.SaveRouteResponse;
import com.routemind.repository.SavedRouteRepository;
import java.util.List;

@Service
public class SavedRouteService {

    @Autowired
    private SavedRouteRepository savedRouteRepository;

    @Autowired
    private UserService userSvc;

    public SaveRouteResponse saveRoute(Long userId, SaveRouteRequest request) {
        // find user
        User user = userSvc.findById(userId);

        // attach user to route
        SavedRoute route = new SavedRoute();
        route.setUser(user);
        route.setRouteName(request.getRouteName());
        route.setStartLat(request.getStart().lat());
        route.setStartLon(request.getStart().lon());
        route.setDestLat(request.getDest().lat());
        route.setDestLon(request.getDest().lon());

        route.setNoiseWeight(request.getNoiseWeight());
        route.setPollutionWeight(request.getPollutionWeight());
        route.setLightingWeight(request.getLightingWeight());
        route.setWheelchairWeight(request.getWheelchairWeight());

        // return saved route
        return new SaveRouteResponse(savedRouteRepository.save(route));
    }

    public List<SaveRouteResponse> getSavedRoutesByUserId(Long userId) {
        return savedRouteRepository.findByUserUserId(userId).stream()
                .map(SaveRouteResponse::new).toList();
    }

    public List<SaveRouteResponse> searchRoutes(Long userId, String routeName) {
        return savedRouteRepository.findByUserUserIdAndRouteNameContainingIgnoreCase(userId, routeName).stream()
                .map(SaveRouteResponse::new).toList();
    }

    public void deleteRoute(Long userId, Long routeId) {
        SavedRoute route = savedRouteRepository.findById(routeId)
                .orElseThrow(() -> new NotFoundError("Route with id " + routeId + " does not exist!"));
        if (route.getUser() == null || !route.getUser().getUserId().equals(userId)) {
            throw new ForbiddenError("Forbidden: User " + userId + " is not authorized to delete route " + routeId);
        }
        savedRouteRepository.delete(route);
    }
}
