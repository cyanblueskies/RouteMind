package com.routemind.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.routemind.entity.HazardReport;

import java.time.LocalDateTime;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class HazardResponse {

    public static HazardResponse from(HazardReport report) {
        HazardResponse res = new HazardResponse();
        res.id = report.getId();
        res.latitude = report.getLatitude();
        res.longitude = report.getLongitude();
        res.description = report.description;
        res.locationDescription = report.locationDescription;
        res.authorUserName = report.getAuthor().getUsername();
        res.reportedAt = report.reportedAt;
        res.upvotes = report.getUpvotes();
        res.hazardType = report.getHazardType();
        return res;
    }

    private Long id;
    private String authorUserName;

    private double latitude;
    private double longitude;
    private String locationDescription;
    private String description;
    private LocalDateTime reportedAt;
    private int upvotes;
    private String hazardType;
}
