package uob.codecollective.backend.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SavedRouteTest {

    @Test
    void settersAndGettersWork() {
        SavedRoute route = new SavedRoute();
        route.setRouteName("Home to Library");
        route.setStartLat(52.48);
        route.setStartLon(-1.89);
        route.setDestLat(52.50);
        route.setDestLon(-1.85);
        route.setNoiseWeight(80);
        route.setPollutionWeight(60);
        route.setLightingWeight(40);
        route.setWheelchairWeight(100);

        assertEquals("Home to Library", route.getRouteName());
        assertEquals(52.48, route.getStartLat(), 0.001);
        assertEquals(-1.89, route.getStartLon(), 0.001);
        assertEquals(52.50, route.getDestLat(), 0.001);
        assertEquals(-1.85, route.getDestLon(), 0.001);
        assertEquals(80, route.getNoiseWeight());
        assertEquals(60, route.getPollutionWeight());
        assertEquals(40, route.getLightingWeight());
        assertEquals(100, route.getWheelchairWeight());
    }

    @Test
    void userAssociation() {
        SavedRoute route = new SavedRoute();
        User user = new User("alice");
        user.setUserId(1L);
        route.setUser(user);

        assertEquals("alice", route.getUser().getUsername());
        assertEquals(1L, route.getUser().getUserId());
    }

    @Test
    void newRouteHasNullId() {
        SavedRoute route = new SavedRoute();
        assertNull(route.getRouteId());
    }
}
