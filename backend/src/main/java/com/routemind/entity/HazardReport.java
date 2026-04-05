package com.routemind.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "hazards")
public class HazardReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User author;

    private double latitude;
    private double longitude;

    protected HazardReport() {}

    public HazardReport(double latitude, double longitude, String description, String locationDescription, LocalDateTime reportedAt, User author, String hazardType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.locationDescription = locationDescription;
        this.upvotes = 0;
        this.reportedAt = reportedAt;
        this.author = author;
        this.hazardType = hazardType;
    }


    public String locationDescription;
    public Severity severity;
    public String description;
    public String hazardType;

    @Column(updatable = false)
    public LocalDateTime reportedAt;

    private int upvotes;
    private State state;

    public State getState() {
        return this.state;
    }

    public void upvote() {
        this.upvotes++;

        if (this.upvotes > 10 && this.state == State.Submitted) {
            this.state = State.Verified;
        }
    }

    public void reject() {
        this.state = State.Rejected;
    }

    public void delete() {
        this.state = State.Deleted;
    }

    public Long getId() {
        return this.id;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public User getAuthor() {
        return author;
    }

    public int getUpvotes() {
        return upvotes;
    }

    public String getHazardType() {
        return hazardType;
    }
}

@Embeddable
class Location {
    public long latitude;
    public long longtitude;
}

enum Severity {
    Low,
    Medium,
    High,
}

enum State {
    Submitted,
    Rejected,
    Verified,
    Expired,
    Deleted
}