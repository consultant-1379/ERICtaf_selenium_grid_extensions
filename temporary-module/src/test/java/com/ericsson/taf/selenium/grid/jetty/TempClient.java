package com.ericsson.taf.selenium.grid.jetty;

import com.ericsson.taf.sikuli.client.SikuliClient;
import org.sikuli.api.Screen;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.robot.Mouse;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.23.
 */
public class TempClient {

    public static void main(String[] args) {
        SikuliClient sikuliClient = new SikuliClient("localhost", 8080, "123");
        Mouse mouse = sikuliClient.getMouse();

        ScreenLocation location = mouse.getLocation();
        Screen screen = location.getScreen();


    }

}
