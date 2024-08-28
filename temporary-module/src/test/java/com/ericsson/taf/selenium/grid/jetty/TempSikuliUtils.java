package com.ericsson.taf.selenium.grid.jetty;

import org.sikuli.api.*;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
public class TempSikuliUtils {

    private static DesktopMouse mouse;
    private static DesktopKeyboard keyboard;
    private static Clipboard clipboard;

    static {
        mouse = new DesktopMouse();
        keyboard = new DesktopKeyboard();
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    public static String getImagePath(File imageFolder, String name) {
        File imageFile = new File(imageFolder, name);
        return imageFile.getAbsolutePath();
    }

    public static void run(String command) {
        ProcessBuilder pb = new ProcessBuilder(command);
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static ScreenLocation topLeft(ScreenRegion region) {
        return Relative.to(region).topLeft().getScreenLocation();
    }

    public static void drawArc(ScreenLocation location, int radius, int from, int to, int step) {
        for (int i = from; i <= to; i += step) {
            double angle = i * Math.PI / 180;
            int dx = (int) Math.round(radius * Math.cos(angle));
            int dy = (int) Math.round(radius * Math.sin(angle));
            ScreenLocation pt = relativeLocation(location, dx, dy);
            drag(pt);
        }
        drop();
    }

    public static void drag() {
        mouse.mouseDown(InputEvent.BUTTON1_MASK);
    }

    public static void drag(ScreenLocation location) {
        mouse.drag(location);
    }

    public static void drop() {
        pause(1000);
        mouse.mouseUp(InputEvent.BUTTON1_MASK);
    }

    public static void pause(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void waitFor(String imageFile, int waitMillis) {
        find(imageFile, waitMillis);
    }

    public static ScreenRegion find(String imageFile, int waitMillis) {
        ScreenRegion region = new DesktopScreenRegion();
        Target target = makeTarget(imageFile);
        return region.wait(target, waitMillis);
    }

    public static ScreenRegion find(String imageFile) {
        ScreenRegion region = new DesktopScreenRegion();
        Target target = makeTarget(imageFile);
        return region.find(target);
    }

    public static Target makeTarget(String targetLocation) {
        File file = new File(targetLocation);
        return new ImageTarget(file);
    }

    public static void sendKey(int keyCode, int... modifierKeyCodes) {
        pressKeys(modifierKeyCodes);
        keyboard.keyDown(keyCode);
        keyboard.keyUp(keyCode);
        releaseKeys(modifierKeyCodes);
    }

    public static void sendKeys(String keys, int... modifierKeyCodes) {
        pressKeys(modifierKeyCodes);
        keyboard.type(keys);
        releaseKeys(modifierKeyCodes);
    }

    private static void pressKeys(int... keyCodes) {
        for (int keyCode : keyCodes) {
            keyboard.keyDown(keyCode);
        }
    }

    private static void releaseKeys(int... keyCodes) {
        for (int keyCode : keyCodes) {
            keyboard.keyUp(keyCode);
        }
    }

    public static void saveClipboard(String directory) {
        if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
            Image image = (Image) getClipboard(clipboard, DataFlavor.imageFlavor);
            File file = timestampedFile(directory, "Clipboard", "png");
            try {
                ImageIO.write(toBufferedImage(image), "png", file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            String text = (String) getClipboard(clipboard, DataFlavor.stringFlavor);
            File file = timestampedFile(directory, "Clipboard", "txt");
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.println(text);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Object getClipboard(Clipboard clipboard, DataFlavor dataFlavor) {
        try {
            return clipboard.getData(dataFlavor);
        } catch (UnsupportedFlavorException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static File timestampedFile(String directory, String prefix, String extension) {
        String name = (prefix != null ? prefix + " " : "") + DATE_FORMAT.format(new Date());
        return new File(directory, name + "." + extension);
    }

    public static BufferedImage toBufferedImage(Image image) {
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        int type = BufferedImage.TYPE_INT_ARGB;
        BufferedImage bufferedImage = new BufferedImage(width, height, type);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return bufferedImage;
    }

    public static ScreenLocation relativeLocation(ScreenLocation location, int dx, int dy) {
        return Relative.to(location).right(dx).below(dy).getScreenLocation();
    }

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH.mm.ss");


}
