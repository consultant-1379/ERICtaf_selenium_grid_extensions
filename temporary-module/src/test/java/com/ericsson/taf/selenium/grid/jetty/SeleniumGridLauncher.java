package com.ericsson.taf.selenium.grid.jetty;

import org.apache.commons.io.FileUtils;
import org.openqa.grid.selenium.GridLauncherV3;

import java.io.File;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         01/10/2015
 */
public class SeleniumGridLauncher {

    private static final String LOG_DIR = "target/selenium-logs";

    private static final String HUB_CONFIG = "hubConfig.json";
    private static final String NODE_CONFIG = "nodeConfig.json";

    private static final String HUB_PARAMS = "-role hub -hubConfig " + HUB_CONFIG;
    private static final String NODE_PARAMS = "-role node -nodeConfig " + NODE_CONFIG;

    public static void main(String[] args) throws Exception {
        launchGrid();
    }

    public static void launchGrid() throws Exception {
        FileUtils.forceMkdir(new File(LOG_DIR));

        GridLauncherV3.main(HUB_PARAMS.split(" "));
        System.out.println("Hub started");

        GridLauncherV3.main(NODE_PARAMS.split(" "));
        System.out.println("Node started");
    }
}
