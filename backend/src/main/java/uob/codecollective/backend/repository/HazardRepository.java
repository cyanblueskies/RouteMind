package uob.codecollective.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uob.codecollective.backend.entity.HazardReport;

import java.util.List;

@Repository
public interface HazardRepository extends JpaRepository<HazardReport, Long> {
    @Query("""
        SELECT l FROM HazardReport l
        WHERE SQRT(POWER(l.latitude - :latitude, 2) + POWER(l.longitude - :longitude, 2)) <= :distance
    """)
    List<HazardReport> findWithinRange(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distance
    );
}
