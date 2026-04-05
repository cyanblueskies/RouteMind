package com.routemind.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.ResponsePath;
import static com.graphhopper.json.Statement.If;
import static com.graphhopper.json.Statement.Op.MULTIPLY;
import com.graphhopper.util.CustomModel;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.PointList;
import com.graphhopper.util.shapes.GHPoint;

import jakarta.annotation.Nullable;
import com.routemind.dto.EnvironmentalScores;
import com.routemind.dto.Point;
import com.routemind.dto.Route;
import com.routemind.dto.RouteRequest;
import com.routemind.dto.RouteSegment;
import com.routemind.dto.RouteSegmentConverter;

@Service
@Transactional
public class RouterService {

    private final GraphHopper hopper;
    private final boolean routerEnabled;
    private final EnvironmentalScorerService environmentalScorer;

    @Autowired
    public RouterService(@Nullable GraphHopper hopper, @Value("${router.enabled}") boolean routerEnabled, EnvironmentalScorerService environmentalScorer) {
        this.hopper = hopper;
        this.routerEnabled = routerEnabled;
        this.environmentalScorer = environmentalScorer;
    }

    public List<Route> create(RouteRequest request) {

        // validate input
        if (request == null || request.getStart() == null || request.getDest() == null) {
            throw new IllegalArgumentException("Start and end points must be provided");
        }

        // fallback to mock route if router is not enabled or hopper is not available
        if (!routerEnabled || hopper == null) {
            return List.of(mockRoute(request));
        }

        // build custom model for GraphHopper from user's weights (deterministic routing)
        CustomModel model = new CustomModel();

        int noise = normalizeWeights(request.getNoiseWeight());
        int pollution = normalizeWeights(request.getPollutionWeight());
        int lighting = normalizeWeights(request.getLightingWeight());
        int wheelchair = normalizeWeights(request.getWheelchairWeight());

        // assume noise and pollution are affected by road traffic
        double badRoadPenalty = 1.0 - Math.min(0.85, (noise + pollution) / 200.0 * 0.85);
        // assume good pedestrian experience in FOOTWAY, PATH, PEDESTRIAN, LIVING_STREET
        double goodPedestrianReward = 1.0 + Math.min(1.20, (noise + pollution + lighting + wheelchair) / 200.0);
        // assume good wheelchair experience in pedestrian roads, but not considering road surface, incline, etc.
        double wheelchairReward = 1.0 + Math.min(1.50, wheelchair / 100.0 * 1.50);

        // priority mapping
        model.addToPriority(If("road_class == FOOTWAY", MULTIPLY, String.valueOf(goodPedestrianReward)));
        model.addToPriority(If("road_class == PATH", MULTIPLY, String.valueOf(goodPedestrianReward)));
        model.addToPriority(If("road_class == PEDESTRIAN", MULTIPLY, String.valueOf(goodPedestrianReward)));
        model.addToPriority(If("road_class == LIVING_STREET", MULTIPLY, String.valueOf(goodPedestrianReward)));
        model.addToPriority(If("road_class == CYCLEWAY", MULTIPLY, "0.9"));

        model.addToPriority(If("road_class == RESIDENTIAL", MULTIPLY, String.valueOf(1.0 + lighting / 300.0)));
        model.addToPriority(If("road_class == TERTIARY", MULTIPLY, "0.85"));
        model.addToPriority(If("road_class == SECONDARY", MULTIPLY, String.valueOf(badRoadPenalty)));
        model.addToPriority(If("road_class == PRIMARY", MULTIPLY, String.valueOf(Math.max(0.10, badRoadPenalty - 0.15))));
        model.addToPriority(If("road_class == TRUNK", MULTIPLY, "0.05"));
        model.addToPriority(If("road_class == MOTORWAY", MULTIPLY, "0"));

        // if wheelchair access is required, apply the wheelchair reward
        if (wheelchair > 0) {
            model.addToPriority(If("road_class == FOOTWAY", MULTIPLY, String.valueOf(wheelchairReward)));
            model.addToPriority(If("road_class == PEDESTRIAN", MULTIPLY, String.valueOf(wheelchairReward)));
        }

        model.setDistanceInfluence(30 + (noise + pollution + lighting + wheelchair) * 0.4);


        // call GraphHopper to generate the routes
        GHRequest ghRequest = new GHRequest(request.getStart().toGHPoint(), request.getDest().toGHPoint());
        ghRequest.setProfile("foot");
        ghRequest.setLocale(Locale.UK);
        ghRequest.setCustomModel(model);

        ghRequest.setPathDetails(List.of("road_class", "surface", "smoothness"));
        ghRequest.setAlgorithm(Parameters.Algorithms.ALT_ROUTE);
        // generate maximum 5 paths
        ghRequest.getHints().putObject(Parameters.Algorithms.AltRoute.MAX_PATHS, 5);
        ghRequest.getHints().putObject("altenative_route.max_weight_factor", 3.0);
        ghRequest.getHints().putObject("altenative_route.max_share_factor", 0.9);
        ghRequest.getHints().putObject("algorithm", "alternative_route");

        GHResponse response = hopper.route(ghRequest);

        if (response.hasErrors()) {
            throw new RuntimeException(response.getErrors().toString());
        }

        List<Route> routes = new ArrayList<>();
        int index = 1;

        for (ResponsePath path : response.getAll()) {
            PointList pointList = path.getPoints();
            List<Point> points = new ArrayList<>();
            for (GHPoint ghPoint : pointList) {
                points.add(Point.from(ghPoint));
            }

            // convert points to segments using real OSM data from path details
            List<RouteSegment> segments = RouteSegmentConverter.fromPathDetails(points, path.getPathDetails());
            EnvironmentalScores scores = environmentalScorer.scoreRoute(segments);

            double noiseScore = round2(scores.noiseScore());
            double pollutionScore = round2(scores.pollutionScore());
            double lightingScore = round2(scores.lightingScore());
            double wheelchairScore = round2(scores.wheelchairScore());

            double distanceKm = round2(path.getDistance() / 1000.0);
            double durationMin = round2(path.getTime() / 60000.0);

            double totalScore = getAverage(noiseScore, pollutionScore, lightingScore, wheelchairScore, request);
            routes.add(new Route("route_" + index++, points, totalScore, noiseScore, pollutionScore, lightingScore, wheelchairScore, distanceKm, durationMin));
        }

        if (routes.isEmpty()) {
            throw new RuntimeException("No routes found!");
        }

        routes.sort(Comparator.comparingDouble(Route::totalScore).reversed());

        return routes.size() > 5 ? routes.subList(0, 5) : routes;
    }

    // for testing, returns a mock route with fixed scores
    private Route mockRoute(RouteRequest request) {
        List<Point> points = List.of(request.getStart(), request.getDest());
        List<RouteSegment> segments = RouteSegmentConverter.fromPoints(points);
        EnvironmentalScores scores = environmentalScorer.scoreRoute(segments);

        double noiseScore = round2(scores.noiseScore());
        double pollutionScore = round2(scores.pollutionScore());
        double lightingScore = round2(scores.lightingScore());
        double wheelchairScore = round2(scores.wheelchairScore());
        double totalScore = getAverage(noiseScore, pollutionScore, lightingScore, wheelchairScore, request);

        double distanceKm = round2(segments.stream().mapToDouble(RouteSegment::getDistanceMeters).sum() / 1000.0);
        double durationMin = round2((distanceKm / 5.0) * 60.0); // asume average speed of 5 km/h

        return new Route("mock_route", points, totalScore, noiseScore, pollutionScore, lightingScore, wheelchairScore, distanceKm, durationMin);
    }

    private int normalizeWeights(int value) {
        return Math.max(0, Math.min(10, value)) * 10;
    }

    private double round2(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private double getAverage(double noise, double pollution, double lighting, double wheelchair, RouteRequest request) {
        int noiseWeight = normalizeWeights(request.getNoiseWeight());
        int pollutionWeight = normalizeWeights(request.getPollutionWeight());
        int lightingWeight = normalizeWeights(request.getLightingWeight());
        int wheelchairWeight = normalizeWeights(request.getWheelchairWeight());
        int total = noiseWeight + pollutionWeight + lightingWeight + wheelchairWeight;

        if (total == 0) {
            return (noise + pollution + lighting + wheelchair) / 4.0;
        }

        double weightTotal = noise * noiseWeight
            + pollution * pollutionWeight
            + lighting * lightingWeight
            + wheelchair * wheelchairWeight;
        return weightTotal / total;
    }
}
