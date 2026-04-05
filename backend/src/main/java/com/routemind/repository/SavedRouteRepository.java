package com.routemind.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.routemind.entity.SavedRoute;

@Repository
public interface SavedRouteRepository extends JpaRepository<SavedRoute, Long> {

    // Get saved routes by user ID
    List<SavedRoute> findByUserUserId(Long userId);

    // Get saved routes by user ID and route name (ignoring cases)
    List<SavedRoute> findByUserUserIdAndRouteNameContainingIgnoreCase(Long userId, String routeName);
}