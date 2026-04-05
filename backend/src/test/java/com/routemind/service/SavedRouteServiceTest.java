package com.routemind.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.dto.Point;
import com.routemind.dto.SaveRouteRequest;
import com.routemind.dto.SaveRouteResponse;
import com.routemind.entity.SavedRoute;
import com.routemind.entity.User;
import com.routemind.repository.SavedRouteRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SavedRouteServiceTest {

    private SavedRouteRepository repo;
    private UserService userSvc;
    private SavedRouteService service;

    @BeforeEach
    void setUp() {
        repo = mock(SavedRouteRepository.class);
        userSvc = mock(UserService.class);
        service = new SavedRouteService();

        // inject mocks via reflection (since @Autowired fields)
        try {
            var repoField = SavedRouteService.class.getDeclaredField("savedRouteRepository");
            repoField.setAccessible(true);
            repoField.set(service, repo);

            var userField = SavedRouteService.class.getDeclaredField("userSvc");
            userField.setAccessible(true);
            userField.set(service, userSvc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private User makeUser(Long id, String name) {
        User u = new User(name);
        u.setUserId(id);
        return u;
    }

    @Test
    void saveRouteSuccessfully() {
        User user = makeUser(1L, "alice");
        when(userSvc.findById(1L)).thenReturn(user);

        SavedRoute saved = new SavedRoute();
        saved.setRouteId(10L);
        saved.setRouteName("Test Route");
        when(repo.save(any())).thenReturn(saved);

        SaveRouteRequest req = new SaveRouteRequest();
        req.setRouteName("Test Route");
        req.setStart(new Point(52.48, -1.89));
        req.setDest(new Point(52.49, -1.90));
        req.setNoiseWeight(50);
        req.setPollutionWeight(50);
        req.setLightingWeight(50);
        req.setWheelchairWeight(0);

        SaveRouteResponse res = service.saveRoute(1L, req);
        assertNotNull(res);
        verify(repo).save(any(SavedRoute.class));
    }

    @Test
    void saveRouteThrowsWhenUserNotFound() {
        Long user_id = 99L;
        when(userSvc.findById(user_id)).thenThrow(
                new NotFoundError("User with id " + user_id + " does not exist!")
        );

        SaveRouteRequest req = new SaveRouteRequest();
        req.setRouteName("Test");
        req.setStart(new Point(52.48, -1.89));
        req.setDest(new Point(52.49, -1.90));

        assertThrows(NotFoundError.class, () -> service.saveRoute(user_id, req));
    }

    @Test
    void getSavedRoutesByUserIdReturnsEmptyList() {
        when(repo.findByUserUserId(1L)).thenReturn(List.of());

        List<SaveRouteResponse> result = service.getSavedRoutesByUserId(1L);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSavedRoutesByUserIdReturnsList() {
        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setRouteName("Home to Work");
        when(repo.findByUserUserId(1L)).thenReturn(List.of(route));

        List<SaveRouteResponse> result = service.getSavedRoutesByUserId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void deleteRouteByOwnerSucceeds() {
        User user = makeUser(1L, "alice");
        SavedRoute route = new SavedRoute();
        route.setRouteId(10L);
        route.setUser(user);
        when(repo.findById(10L)).thenReturn(Optional.of(route));

        service.deleteRoute(1L, 10L);
        verify(repo).delete(route);
    }

    @Test
    void deleteRouteByNonOwnerThrows() {
        User user = makeUser(1L, "alice");
        SavedRoute route = new SavedRoute();
        route.setRouteId(10L);
        route.setUser(user);
        when(repo.findById(10L)).thenReturn(Optional.of(route));

        assertThrows(RuntimeException.class, () -> service.deleteRoute(2L, 10L));
    }

    @Test
    void deleteNonExistentRouteThrows() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.deleteRoute(1L, 999L));
    }

    @Test
    void searchRoutesReturnsFilteredResults() {
        SavedRoute route = new SavedRoute();
        route.setRouteId(1L);
        route.setRouteName("Route to Library");
        when(repo.findByUserUserIdAndRouteNameContainingIgnoreCase(1L, "Library"))
                .thenReturn(List.of(route));

        List<SaveRouteResponse> result = service.searchRoutes(1L, "Library");
        assertEquals(1, result.size());
    }
}
