package com.routemind;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RouteMindApplicationTests {

    @Test
    void contextLoads() {
    }

    @Autowired
    private MockMvc mockMvc;

    /** Helper: sign up a user and return the JWT token */
    private String signUpAndGetToken(String username) throws Exception {
        String response = mockMvc.perform(
            post("/api/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"testpass123\"}")
        )
        .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.token");
    }

    /** Helper: sign up and return userId as string */
    private String signUpAndGetUserId(String username) throws Exception {
        String response = mockMvc.perform(
            post("/api/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"" + username + "\",\"password\":\"testpass123\"}")
        )
        .andReturn().getResponse().getContentAsString();
        return com.jayway.jsonpath.JsonPath.read(response, "$.userId").toString();
    }

    @Test
    void createUser() throws Exception {
        mockMvc.perform(
            post("/api/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testusercreate\",\"password\":\"testpass123\"}")
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.token").exists())
        .andExpect(jsonPath("$.userId").exists())
        .andExpect(jsonPath("$.username").value("testusercreate"));
    }

    @Test
    void loginUser() throws Exception {
        // First sign up
        mockMvc.perform(
            post("/api/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"loginuser\",\"password\":\"testpass123\"}")
        );

        // Then login
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"loginuser\",\"password\":\"testpass123\"}")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void createHazard() throws Exception {
        String token = signUpAndGetToken("testuseridcreate");

        String hazardJson = """
        {
            "latitude": 51.5074,
            "longitude": -0.1278,
            "description": "Test hazard"
        }
        """;

        mockMvc.perform(
            post("/api/hazards/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content(hazardJson)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void upvoteHazard() throws Exception {
        String token1 = signUpAndGetToken("testuseridupvote");

        String hazardResponse = mockMvc.perform(
            post("/api/hazards/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "latitude": 51.5074,
                    "longitude": -0.1278,
                    "description": "Test hazard for upvote"
                }
                """)
                .header("Authorization", "Bearer " + token1)
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String hazardId = com.jayway.jsonpath.JsonPath.read(hazardResponse, "$.id").toString();

        // Different user upvotes
        String token2 = signUpAndGetToken("testuseridupvote2");

        mockMvc.perform(
            patch("/api/hazards/upvote/" + hazardId)
                .header("Authorization", "Bearer " + token2)
        )
        .andExpect(status().isOk());
    }

    @Test
    void getHazard() throws Exception {
        String token = signUpAndGetToken("testuseridget");

        String hazardResponse = mockMvc.perform(
            post("/api/hazards/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "latitude": 51.5074,
                    "longitude": -0.1278,
                    "description": "Test hazard for get"
                }
                """)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String hazardId = com.jayway.jsonpath.JsonPath.read(hazardResponse, "$.id").toString();

        mockMvc.perform(
            get("/api/hazards/get/" + hazardId)
        )
        .andExpect(status().isOk());
    }

    @Test
    void deleteHazard() throws Exception {
        String token = signUpAndGetToken("testuseridhdelete");

        String hazardResponse = mockMvc.perform(
            post("/api/hazards/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "latitude": 51.5074,
                    "longitude": -0.1278,
                    "description": "Test hazard for delete"
                }
                """)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String hazardId = com.jayway.jsonpath.JsonPath.read(hazardResponse, "$.id").toString();

        mockMvc.perform(
            delete("/api/hazards/delete/" + hazardId)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk());
    }

    @Test
    void getnearbyHazards() throws Exception {
        String token = signUpAndGetToken("testuseridnearby");

        mockMvc.perform(
            post("/api/hazards/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                {
                    "latitude": 51.5074,
                    "longitude": -0.1278,
                    "description": "Test hazard for nearby"
                }
                """)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isCreated());

        mockMvc.perform(
            get("/api/hazards/nearby")
                .param("lat", "51.5074")
                .param("long", "-0.1278")
                .param("distance", "1000")
        )
        .andExpect(status().isOk());
    }

    @Test
    void savedRoute() throws Exception {
        String token = signUpAndGetToken("testusersavedroute");

        String testSavedRouteCreate = """
        {
            "routeName": "Test Route",
            "start": { "lat": 51.5074, "lon": -0.1278 },
            "dest": { "lat": 51.5155, "lon": -0.1420 },
            "noiseWeight": 1,
            "pollutionWeight": 1,
            "lightingWeight": 1,
            "wheelchairWeight": 1
        }
        """;

        String savedRoute = mockMvc.perform(
            post("/api/saved-routes/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(testSavedRouteCreate)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.routeId").exists())
        .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
            get("/api/saved-routes/")
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].routeName").value("Test Route"));

        String routeId = com.jayway.jsonpath.JsonPath.read(savedRoute, "$.routeId").toString();
        mockMvc.perform(
            delete("/api/saved-routes/" + routeId)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isOk());
    }

    @Test
    void postUserPreference() throws Exception {
        String token = signUpAndGetToken("testuserpreferencepost");

        String prefJson = """
        {
            "noiseWeight": 1,
            "lightingWeight": 2,
            "wheelchairWeight": 3,
            "pollutionWeight": 4
        }
        """;

        mockMvc.perform(
            post("/api/user/preference/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prefJson)
                .header("Authorization", "Bearer " + token)
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").exists());
    }

    @Test
    void getUserPreference() throws Exception {
        String token = signUpAndGetToken("testuserpreferenceget");
        String userId = signUpAndGetUserId("testuserpreferenceget2");

        // save preference first using token for this user
        String token2 = signUpAndGetToken("testuserpreferenceget3");

        String prefJson = """
        {
            "noiseWeight": 1,
            "lightingWeight": 2,
            "wheelchairWeight": 3,
            "pollutionWeight": 4
        }
        """;

        mockMvc.perform(
            post("/api/user/preference/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prefJson)
                .header("Authorization", "Bearer " + token2)
        )
        .andExpect(status().isCreated());

        // Get the userId from the token2 user to query preferences
        String response = mockMvc.perform(
            post("/api/users/new")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuserpreferenceget4\",\"password\":\"testpass123\"}")
        ).andReturn().getResponse().getContentAsString();
        String uid = com.jayway.jsonpath.JsonPath.read(response, "$.userId").toString();

        // Save a preference for this user
        mockMvc.perform(
            post("/api/user/preference/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(prefJson)
                .header("Authorization", "Bearer " + com.jayway.jsonpath.JsonPath.read(response, "$.token").toString())
        ).andExpect(status().isCreated());

        mockMvc.perform(
            get("/api/user/preference/" + uid)
        ).andExpect(status().isAccepted());
    }
}
