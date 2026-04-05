package com.routemind.entity;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class HazardReportTest {

    private User makeUser(Long id, String name) {
        User u = new User(name, "pass");
        u.setUserId(id);
        return u;
    }

    @Test
    void newHazardHasZeroUpvotes() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "Broken path",
                "High Street", LocalDateTime.now(), author, "Physical");

        assertEquals(0, report.getUpvotes());
    }

    @Test
    void upvoteIncrementsCount() {
        User author = makeUser(1L, "alice");
        HazardReport report = new HazardReport(52.48, -1.89, "Ice",
                "Park Road", LocalDateTime.now(), author, "Environmental");

        report.upvote();
        assertEquals(1, report.getUpvotes());

        report.upvote();
        assertEquals(2, report.getUpvotes());
    }

    @Test
    void gettersReturnCorrectValues() {
        User author = makeUser(1L, "bob");
        HazardReport report = new HazardReport(52.50, -1.85, "Broken lamp",
                "Station Road", LocalDateTime.now(), author, "Infrastructure");

        assertEquals(52.50, report.getLatitude(), 0.001);
        assertEquals(-1.85, report.getLongitude(), 0.001);
        assertEquals("bob", report.getAuthor().getUsername());
        assertEquals("Infrastructure", report.getHazardType());
    }

    @Test
    void hazardTypeStoredCorrectly() {
        User author = makeUser(1L, "alice");

        HazardReport physical = new HazardReport(52.48, -1.89, "d", "l",
                LocalDateTime.now(), author, "Physical");
        HazardReport environmental = new HazardReport(52.48, -1.89, "d", "l",
                LocalDateTime.now(), author, "Environmental");
        HazardReport infrastructure = new HazardReport(52.48, -1.89, "d", "l",
                LocalDateTime.now(), author, "Infrastructure");

        assertEquals("Physical", physical.getHazardType());
        assertEquals("Environmental", environmental.getHazardType());
        assertEquals("Infrastructure", infrastructure.getHazardType());
    }
}
