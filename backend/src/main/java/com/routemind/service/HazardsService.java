package com.routemind.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import com.routemind.controller.errors.ForbiddenError;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.dto.CreateHazardRequest;
import com.routemind.dto.HazardResponse;
import com.routemind.entity.HazardReport;
import com.routemind.entity.User;
import com.routemind.repository.HazardRepository;

@Service
@Transactional
public class HazardsService {
    private final HazardRepository repo;

    private final UserService userSvc;

    public HazardsService(HazardRepository repo, UserService userSvc) {
        this.repo = repo;
        this.userSvc = userSvc;
    }

    public HazardResponse create(CreateHazardRequest req, Long userId) {
        User author = userSvc.findById(userId);

        HazardReport report = new HazardReport(
                req.latitude, req.longitude,
                req.description, req.locationDescription,
                req.reportedAt, author, req.hazardType);
        repo.save(report);
        return HazardResponse.from(report);
    }

    public void upvote(Long hazardId, Long userId) {

        HazardReport report = repo.findById(hazardId).orElseThrow(
                () -> new NotFoundError("Hazard with id " + hazardId + " does not exist!")
        );

        if (report.getAuthor() != null && report.getAuthor().getUserId().equals(userId)) {
            throw new ForbiddenError("User cannot upvote their own report");
        }

        report.upvote();
        repo.save(report);
    }

    public HazardResponse findById(Long hazardId) {
        HazardReport report = repo.findById(hazardId).orElseThrow(
                () -> new NotFoundError("Hazard with id " + hazardId + " does not exist!")
        );
        return HazardResponse.from(report);
    }

    public void deleteById(Long hazardId, Long userId) {
        Optional<HazardReport> report = repo.findById(hazardId);
        if (report.isEmpty()) {
            throw new NotFoundError("Hazard with id " + hazardId + " does not exist!");
        }

        User user = report.get().getAuthor();
        if (user.getUserId().equals(userId)) {
            repo.deleteById(hazardId);
        } else {
            throw new ForbiddenError("User is not the Author of this report.");
        }
    }

    public List<HazardResponse> findNearby(double latitude, double longitude, double distance) {
        return repo.findWithinRange(latitude, longitude, distance)
                .stream()
                .map(HazardResponse::from)
                .toList();
    }
}
