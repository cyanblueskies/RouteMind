package com.routemind.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.graphhopper.GraphHopper;
import com.graphhopper.config.Profile;
import com.graphhopper.json.Statement;
import com.graphhopper.util.CustomModel;


@Configuration
public class GHConfig {

    @Value("${router.enabled:true}")
    private boolean routerEnabled;

    @Bean(destroyMethod = "close")
    public GraphHopper graphHopper() {
        if (!routerEnabled) {
            return null;
        }
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile("england-latest.osm.pbf");
        hopper.setGraphHopperLocation("target/routing-graph-cache");
        hopper.setEncodedValuesString("road_class,foot_access,surface,smoothness");

        CustomModel model = new CustomModel();
        model.addToSpeed(Statement.If("true", Statement.Op.LIMIT, "5"));
        model.addToSpeed(Statement.If("!foot_access", Statement.Op.MULTIPLY, "0"));
        hopper.setProfiles(new Profile("foot").setCustomModel(model));

        hopper.importOrLoad();
        return hopper;
    }
}
