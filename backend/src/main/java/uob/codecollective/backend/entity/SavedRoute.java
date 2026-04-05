package uob.codecollective.backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class SavedRoute {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long routeId;

    private String routeName;
    private double startLat;
    private double startLon;
    private double destLat;
    private double destLon;

    private int noiseWeight;
    private int pollutionWeight;
    private int lightingWeight;
    private int wheelchairWeight;

    // Link to user (Associate saved routes with users)
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public double getStartLat() { return startLat; }
    public void setStartLat(double startLat) { this.startLat = startLat; }

    public double getStartLon() { return startLon; }
    public void setStartLon(double startLon) { this.startLon = startLon; }

    public double getDestLat() { return destLat; }
    public void setDestLat(double destLat) { this.destLat = destLat; }

    public double getDestLon() { return destLon; }
    public void setDestLon(double destLon) { this.destLon = destLon; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getNoiseWeight() { return noiseWeight; }
    public void setNoiseWeight(int noiseWeight) { this.noiseWeight = noiseWeight;   }

    public int getPollutionWeight() { return pollutionWeight; }
    public void setPollutionWeight(int pollutionWeight) { this.pollutionWeight = pollutionWeight; }

    public int getLightingWeight() { return lightingWeight; }
    public void setLightingWeight(int lightingWeight) { this.lightingWeight = lightingWeight;  }

    public int getWheelchairWeight() { return wheelchairWeight; }
    public void setWheelchairWeight(int wheelchairWeight) { this.wheelchairWeight = wheelchairWeight; }

}
