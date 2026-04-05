package uob.codecollective.backend.dto;

import java.util.List;

public class RouteOptionsResponse {
    private List<Route> routes;

    public RouteOptionsResponse() {}

    public RouteOptionsResponse(List<Route> routes) {
        this.routes = routes;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }
}
