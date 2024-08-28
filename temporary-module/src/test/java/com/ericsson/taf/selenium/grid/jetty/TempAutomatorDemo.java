package com.ericsson.taf.selenium.grid.jetty;

import com.ericsson.taf.sikuli.client.SikuliClient;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;
import org.sikuli.api.robot.Mouse;

import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
public class TempAutomatorDemo {

    public static void main(String... args) throws URISyntaxException {
        justDoIt();
    }

    private static void justDoIt() throws URISyntaxException {
        SikuliClient sikuliClient = new SikuliClient("localhost", 8080, "123");
        Mouse desktopMouse = sikuliClient.getMouse();

        URL imageFolderUrl = TempAutomatorDemo.class.getClassLoader().getResource("automation");
        File imageFolder = new File(imageFolderUrl.toURI());

        String brushImagePath = TempSikuliUtils.getImagePath(imageFolder, "mspaint-brush.png");
        String canvasImagePath = TempSikuliUtils.getImagePath(imageFolder, "mspaint-canvas.png");

        File desktopFolder = new File(System.getProperty("user.home"), "Desktop");
        String desktopPath = desktopFolder.getAbsolutePath();


        TempSikuliUtils.run("mspaint");
        TempSikuliUtils.waitFor(brushImagePath, 5000);

        TempSikuliUtils.sendKey(KeyEvent.VK_E, KeyEvent.VK_CONTROL);
        TempSikuliUtils.sendKeys("100");
        TempSikuliUtils.sendKey(KeyEvent.VK_TAB);
        TempSikuliUtils.sendKeys("100");
        TempSikuliUtils.sendKey(KeyEvent.VK_ENTER);

        ScreenRegion canvas = TempSikuliUtils.find(canvasImagePath);
        ScreenLocation center = TempSikuliUtils.topLeft(canvas);
        TempSikuliUtils.drawArc(center, 25, 0, 360, 30);
        TempSikuliUtils.drawArc(center, 15, 15, 165, 30);
        TempSikuliUtils.drawArc(TempSikuliUtils.relativeLocation(center, -9, -9), 2, 0, 360, 60);
        TempSikuliUtils.drawArc(TempSikuliUtils.relativeLocation(center, 9, -9), 2, 0, 360, 60);

        TempSikuliUtils.sendKey(KeyEvent.VK_A, KeyEvent.VK_CONTROL);
        TempSikuliUtils.sendKey(KeyEvent.VK_C, KeyEvent.VK_CONTROL);
        TempSikuliUtils.saveClipboard(desktopPath);
    }

}
