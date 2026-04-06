package com.routemind.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;

import java.io.File;

@Configuration
public class GHConfig {

    @Value("${router.enabled:true}")
    private boolean routerEnabled;

    @Value("${router.osm-file:england-260405.osm.pbf}")
    private String osmFile;

    @Bean(destroyMethod = "close")
    public GraphHopper graphHopper() {
        if (!routerEnabled) {
            System.out.println("[RouteMind] Router disabled via config.");
            return null;
        }

        File osm = new File(osmFile);
        if (!osm.exists()) {
            System.out.println("[RouteMind] OSM file not found: " + osm.getAbsolutePath());
            System.out.println("[RouteMind] Router will be disabled. Download the file with:");
            System.out.println("  wget https://download.geofabrik.de/europe/great-britain/england-latest.osm.pbf");
            return null;
        }

        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(osmFile);
        hopper.setGraphHopperLocation("target/routing-graph-cache");
        hopper.setEncodedValuesString("road_class,foot_access,surface,smoothness");

        CustomModel model = new CustomModel();
        model.addToSpeed(Statement.If("true", Statement.Op.LIMIT, "5"));
        model.addToSpeed(Statement.If("!foot_access", Statement.Op.MULTIPLY, "0"));
        hopper.setProfiles(new Profile("foot").setCustomModel(model));

        hopper.importOrLoad();
        System.out.println("[RouteMind] GraphHopper loaded successfully.");
        return hopper;
    }
}
