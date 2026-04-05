package uob.codecollective.backend.dto;

import java.time.LocalDateTime;

public class CreateHazardRequest {
    public double latitude;
    public double longitude;

    public String locationDescription;
    public String description;
    public LocalDateTime reportedAt;
    public String hazardType;
}
