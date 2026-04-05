package com.routemind.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;

@Entity
public class UserPreference {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long userId;

  @OneToOne
  @MapsId
  @JoinColumn(name = "user_id")
  private User user;

  private int noiseWeight;
  private int lightingWeight;
  private int wheelchairWeight;
  private int pollutionWeight;

  public UserPreference() {}

  public long getUserId() {
      return userId;
  }
  public void setUserId(long userId){
    this.userId = userId;
  }

  public User getUser(){
    return user;
  }

  public void setUser(User user){
    this.user = user;
  }
  public int getNoiseWeight(){
    return noiseWeight;
  }

  public void setNoiseWeight(int noiseWeight){
    this.noiseWeight = noiseWeight;
  }


  public int getLightingWeight(){
    return lightingWeight;
  }

  public void setLightingWeight(int lightingWeight){
    this.lightingWeight = lightingWeight;
  }

  public int getWheelchairWeight(){
    return wheelchairWeight;
  }

  public void setWheelchairWeight(int wheelchairWeight){
    this.wheelchairWeight= wheelchairWeight;
  }

  public int getPollutionWeight(){
    return pollutionWeight;
  }

  public void setPollutionWeight(int pollutionWeight){
    this.pollutionWeight = pollutionWeight;
  }
}
