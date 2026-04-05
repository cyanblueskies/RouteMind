package uob.codecollective.backend.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uob.codecollective.backend.dto.Route;
import uob.codecollective.backend.dto.RouteRequest;
import uob.codecollective.backend.service.RouterService;

@RestController
@RequestMapping("/api/router")
public class RouterController {
    private final RouterService routerService;

    public RouterController(RouterService routerService) {
        this.routerService = routerService;
    }

    @PostMapping("/new")
    public List<Route> createRoute(@RequestBody RouteRequest req) {
        return routerService.create(req);
    }
}
