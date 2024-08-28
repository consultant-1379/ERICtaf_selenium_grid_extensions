package com.ericsson.taf.sikuli.client;

import com.ericsson.taf.rmi.protocol.client.RemoteNavigator;
import com.ericsson.taf.selenium.node.sikuli.rmi.TargetFactory;
import com.ericsson.taf.sikuli.client.download.FileDownloadRequest;
import com.ericsson.taf.sikuli.client.upload.ResourceUploadRequest;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.robot.Keyboard;
import org.sikuli.api.robot.Mouse;

import java.awt.datatransfer.Clipboard;
import java.io.File;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
// TODO: MVO: make it interface, make factory creating both local and remote instances
public class SikuliClient {

    private static final String SIKULI_EXTENSION_PATH = "/grid/admin/HubRequestsProxyingServlet/session/%s/SikuliExtensionServlet";

    protected final RemoteNavigator navigator;
    protected final ResourceUploadRequest resourceUploadRequest;
    protected final FileDownloadRequest fileDownloadRequest;

    public SikuliClient(String host, int port, String sessionId) {
        navigator = new RemoteNavigator(host, port, String.format(SIKULI_EXTENSION_PATH, sessionId));
        resourceUploadRequest = new ResourceUploadRequest(host, port, sessionId);
        fileDownloadRequest = new FileDownloadRequest(host, port, sessionId);
    }

    public Mouse getMouse() {
        return navigator.createProxy(Mouse.class, "mouse");
    }

    public Keyboard getKeyboard() {
        return navigator.createProxy(Keyboard.class, "keyboard");
    }

    public Clipboard getClipboard() {
        return navigator.createProxy(Clipboard.class, "clipboard");
    }

    public DesktopScreenRegion getDesktop() {
        return navigator.createProxy(DesktopScreenRegion.class, "desktop");
    }

    public TargetFactory getTargetFactory() {
        return navigator.createProxy(TargetFactory.class, "target-factory");
    }

    public void uploadResourceBundle(String resourceBundlePath) {
        String absolutePath = resourceUploadRequest.upload(resourceBundlePath);
        getTargetFactory().setImagePrefix(absolutePath);
    }

    public File download(String pathToFile) {
        return fileDownloadRequest.download(pathToFile);
    }
}
