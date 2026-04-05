package com.routemind.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.routemind.controller.errors.NotFoundError;
import com.routemind.dto.CreateHazardRequest;
import com.routemind.dto.HazardResponse;
import com.routemind.entity.HazardReport;
import com.routemind.entity.User;
import com.routemind.repository.HazardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class HazardsServiceTest {

    private HazardRepository repo;
    private UserService userSvc;
    private HazardsService service;

    @BeforeEach
    void setUp() {
        repo = mock(HazardRepository.class);
        userSvc = mock(UserService.class);
        service = new HazardsService(repo, userSvc);
    }

    private User makeUser(Long id, String name) {
        User u = new User(name);
        u.setUserId(id);
        return u;
    }

    @Test
    void createHazardSuccessfully() {
        User author = makeUser(1L, "alice");
        when(userSvc.findById(1L)).thenReturn(author);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CreateHazardRequest req = new CreateHazardRequest();
        req.latitude = 52.48;
        req.longitude = -1.89;
        req.description = "Broken pavement";
        req.locationDescription = "Near station";
        req.hazardType = "Physical";
        req.reportedAt = LocalDateTime.now();

        HazardResponse res = service.create(req, 1L);
        assertNotNull(res);
        verify(repo).save(any(HazardReport.class));
    }

    @Test
    void createHazardThrowsWhenUserNotFound() {
        Long user_id = 99L;
        when(userSvc.findById(user_id)).thenThrow(
                new NotFoundError("User with id " + user_id + " does not exist!")
        );

        CreateHazardRequest req = new CreateHazardRequest();
        req.latitude = 52.48;
        req.longitude = -1.89;

        assertThrows(NotFoundError.class, () -> service.create(req, 99L));
    }

    @Test
    void upvoteIncrementsCount() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "test", "loc",
                LocalDateTime.now(), author, "Physical");

        when(repo.findById(10L)).thenReturn(Optional.of(report));

        // different user upvotes
        service.upvote(10L, 2L);
        verify(repo).save(report);
        assertEquals(1, report.getUpvotes());
    }

    @Test
    void upvoteOwnReportThrows() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "test", "loc",
                LocalDateTime.now(), author, "Physical");

        when(repo.findById(10L)).thenReturn(Optional.of(report));

        assertThrows(RuntimeException.class, () -> service.upvote(10L, 1L));
    }

    @Test
    void upvoteNonExistentHazardThrows() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.upvote(999L, 1L));
    }

    @Test
    void findByIdReturnsHazard() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "test", "loc",
                LocalDateTime.now(), author, "Physical");

        when(repo.findById(10L)).thenReturn(Optional.of(report));

        HazardResponse res = service.findById(10L);
        assertNotNull(res);
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.findById(999L));
    }

    @Test
    void deleteByAuthorSucceeds() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "test", "loc",
                LocalDateTime.now(), author, "Physical");

        when(repo.findById(10L)).thenReturn(Optional.of(report));

        service.deleteById(10L, 1L);
        verify(repo).deleteById(10L);
    }

    @Test
    void deleteByNonAuthorThrows() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "test", "loc",
                LocalDateTime.now(), author, "Physical");

        when(repo.findById(10L)).thenReturn(Optional.of(report));

        assertThrows(RuntimeException.class, () -> service.deleteById(10L, 2L));
    }

    @Test
    void deleteNonExistentHazardThrows() {
        when(repo.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.deleteById(999L, 1L));
    }

    @Test
    void findNearbyReturnsList() {
        when(repo.findWithinRange(52.48, -1.89, 5.0)).thenReturn(List.of());

        List<HazardResponse> result = service.findNearby(52.48, -1.89, 5.0);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
