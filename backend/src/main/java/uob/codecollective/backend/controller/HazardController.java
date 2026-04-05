package uob.codecollective.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import uob.codecollective.backend.dto.CreateHazardRequest;
import uob.codecollective.backend.dto.HazardResponse;
import uob.codecollective.backend.service.HazardsService;

import java.util.List;

@RestController
@RequestMapping("/api/hazards")
public class HazardController {
    private final HazardsService svc;

    public HazardController(HazardsService svc) {
        this.svc = svc;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public HazardResponse create(
            @RequestBody CreateHazardRequest req,
            @CookieValue(value = "user_id", required = true) Long userId
    ) {
        return svc.create(req, userId);
    }

    @PatchMapping("/upvote/{hazardId}")
    public void upvote(
            @CookieValue(value = "user_id", required = true) Long userId,
            @PathVariable long hazardId
    ) {
        svc.upvote(hazardId, userId);
    }

    @GetMapping("/get/{id}")
    public HazardResponse getById(@PathVariable long id) {
        return svc.findById(id);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteById(
            @CookieValue(value = "user_id", required = true) Long userId,
            @PathVariable long id
    ) {
        svc.deleteById(id, userId);
    }

    @GetMapping("/nearby")
    public List<HazardResponse> getHazardsNear(
            @RequestParam("lat") double latitude,
            @RequestParam("long") double longitude,
            @RequestParam(name = "distance", required = false) double distance
    ) {
        return svc.findNearby(latitude, longitude, distance);
    }
}
