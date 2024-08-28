package com.ericsson.taf.sikuli.client;


import com.ericsson.taf.selenium.node.sikuli.components.UiComponent;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.ImageTarget;
import org.sikuli.api.ScreenLocation;
import org.sikuli.api.ScreenRegion;

import java.io.File;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
public class TafSikuliClient {

    private SikuliClient sikuliClient;

    public TafSikuliClient(String host, int port, String sessionId) {
        sikuliClient = new SikuliClient(host, port, sessionId);
    }

    public void uploadResourceBundle(String resourceBundlePath) {
        sikuliClient.uploadResourceBundle(resourceBundlePath);
    }

    public File download(String pathToFile) {
        return sikuliClient.download(pathToFile);
    }

    public UiComponent find(String imageFile) {
        ImageTarget imageTarget = sikuliClient.getTargetFactory().createImageTarget(imageFile);
        return new ImageComponent(imageTarget, sikuliClient);
    }

    private static final class ImageComponent implements UiComponent {

        private ImageTarget imageTarget;

        private SikuliClient sikuliClient;

        public ImageComponent(ImageTarget imageTarget, SikuliClient sikuliClient) {
            this.imageTarget = imageTarget;
            this.sikuliClient = sikuliClient;
        }

        @Override
        public void click() {
            DesktopScreenRegion desktop = sikuliClient.getDesktop();
            ScreenRegion screenRegion = desktop.find(imageTarget);
            ScreenLocation center = screenRegion.getCenter();
            sikuliClient.getMouse().click(center);
        }
    }


}
