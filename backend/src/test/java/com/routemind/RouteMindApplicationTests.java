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

    @Test
    void createUser() throws Exception {
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testusercreate")
    )
        .andExpect(status().isCreated());
    }

    @Test
    void loginUser() throws Exception {
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("login")
        );

        mockMvc.perform(
            post("/api/users/login").contentType(MediaType.TEXT_PLAIN).content("login")
        )
        .andExpect(status().isAccepted());
    }

    @Test
    void createHazard() throws Exception {
        String testuseridcreate = 
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridcreate")
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();
    


        String testhazardcreate = """
        {
            "latitude": 51.5074,
            "longitude": -0.1278,
            "description": "Test hazard"
        }
        """;

        mockMvc.perform(
            post("/api/hazards/new").contentType(MediaType.APPLICATION_JSON).content(testhazardcreate).cookie(new jakarta.servlet.http.Cookie("user_id", testuseridcreate))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists());
    }


   
    @Test
    void upvoteHazard() throws Exception {
        String testuseridcreate = 
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridupvote")
        )
        .andReturn().getResponse().getContentAsString();

        String testhazardidcreate =
        mockMvc.perform(
            post("/api/hazards/new").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "latitude": 51.5074,
                "longitude": -0.1278,
                "description": "Test hazard for upvote"
            }
            """).cookie(new jakarta.servlet.http.Cookie("user_id", testuseridcreate))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").exists())
        .andReturn().getResponse().getContentAsString();

        String hazardId = 
        com.jayway.jsonpath.JsonPath.read(testhazardidcreate, "$.id").toString();

        String upvoteUserId = 
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridupvote2")
        )
        .andReturn().getResponse().getContentAsString();

        mockMvc.perform(
            patch("/api/hazards/upvote/" + hazardId).cookie(new jakarta.servlet.http.Cookie("user_id", upvoteUserId))
        )
        .andExpect(status().isOk());
    }


    @Test
    void getHazard() throws Exception {
        String userId =
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridget")
        )
        .andReturn().getResponse().getContentAsString();


        String testhazardidcreate =
        mockMvc.perform(
            post("/api/hazards/new").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "latitude": 51.5074,
                "longitude": -0.1278,
                "description": "Test hazard for get"
            }
            """).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String hazardId = com.jayway.jsonpath.JsonPath.read(testhazardidcreate, "$.id").toString();
        mockMvc.perform(
            get("/api/hazards/get/" + hazardId).contentType(MediaType.TEXT_PLAIN)
        )
        .andExpect(status().isOk());
    }


    @Test
    void deleteHazard() throws Exception {
        String userId =
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridhdelete")
        )
        .andReturn().getResponse().getContentAsString();

         String testhazardidcreate =
        mockMvc.perform(
            post("/api/hazards/new").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "latitude": 51.5074,
                "longitude": -0.1278,
                "description": "Test hazard for delete"
            }
            """).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String hazardId = com.jayway.jsonpath.JsonPath.read(testhazardidcreate, "$.id").toString();

        mockMvc.perform(
            delete("/api/hazards/delete/" + hazardId).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isOk());
    }

    @Test
    void getnearbyHazards() throws Exception {
        String userId =
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuseridnearby")
        )
        .andReturn().getResponse().getContentAsString();

        String testhazardidcreate =
        mockMvc.perform(
            post("/api/hazards/new").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "latitude": 51.5074,
                "longitude": -0.1278,
                "description": "Test hazard for nearby"
            }
            """).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();


        mockMvc.perform(
            get("/api/hazards/nearby").param("lat", "51.5074").param("long", "-0.1278").param("distance", "1000")
        )
        .andExpect(status().isOk());
    }

    @Test
    void savedRoute() throws Exception {
        String userId =
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testusersavedroute")
        )
        .andReturn().getResponse().getContentAsString();

        String testSavedRouteCreate = """
        {
            "routeName": "Test Route",
            "start": {
                "lat":  51.5074,
                "lon": -0.1278
            },
            "dest": {
                "lat": 51.5155,
                "lon": -0.1420
            }, 
            "noiseWeight": 1,
            "pollutionWeight": 1,
            "lightingWeight": 1,
            "wheelchairWeight": 1
        }
        """;

        
        String savedRoute = mockMvc.perform(
            post("/api/saved-routes/").contentType(MediaType.APPLICATION_JSON).content(testSavedRouteCreate).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.routeId").exists())
        .andReturn().getResponse().getContentAsString();
    

        mockMvc.perform(
            get("/api/saved-routes/").cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].routeName").value("Test Route"));

        String routeId = com.jayway.jsonpath.JsonPath.read(savedRoute, "$.routeId").toString();
        mockMvc.perform(
            delete("/api/saved-routes/" + routeId).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isOk());
    }


    @Test
    void postUserPreference() throws Exception {
        String userId = mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuserpreferencepost")
        )
        .andExpect(status().isCreated())
        .andReturn().getResponse().getContentAsString();

        String testPreferencepostCreate = """
        {
            "noiseWeight": 1,
            "lightingWeight": 2,
            "wheelchairWeight": 3,
            "pollutionWeight": 4
        }
        """;
        mockMvc.perform(
            post("/api/user/preference/save").contentType(MediaType.APPLICATION_JSON).content(testPreferencepostCreate).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").exists());
    }

    @Test
    void getUserPreference() throws Exception {
        String userId =
        mockMvc.perform(
            post("/api/users/new").contentType(MediaType.TEXT_PLAIN).content("testuserpreferenceget")
        )
        .andReturn().getResponse().getContentAsString();
        String testperferencegetcreate = """
        {
            "noiseWeight": 1,
            "lightingWeight": 2,
            "wheelchairWeight": 3,
            "pollutionWeight": 4
        }
        """;
        mockMvc.perform(
            post("/api/user/preference/save").contentType(MediaType.APPLICATION_JSON).content(testperferencegetcreate).cookie(new jakarta.servlet.http.Cookie("user_id", userId))
        )
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$").exists());

       mockMvc.perform(
         get("/api/user/preference/" + userId)).andExpect(status().isAccepted());
    }
}
