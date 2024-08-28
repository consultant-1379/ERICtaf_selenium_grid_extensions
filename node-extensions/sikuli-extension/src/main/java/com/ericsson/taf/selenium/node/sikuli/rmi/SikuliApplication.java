package com.ericsson.taf.selenium.node.sikuli.rmi;

import com.ericsson.taf.rmi.protocol.MethodInvocationDto;
import com.ericsson.taf.rmi.protocol.MethodInvocationResultDto;
import com.ericsson.taf.rmi.protocol.server.RmiFacade;
import org.sikuli.api.DesktopScreenRegion;
import org.sikuli.api.robot.desktop.DesktopKeyboard;
import org.sikuli.api.robot.desktop.DesktopMouse;

import java.awt.Toolkit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
public class SikuliApplication {

    private static final Logger LOGGER = Logger.getLogger(SikuliApplication.class.getName());

    private final RmiFacade rmiFacade;

    public SikuliApplication() {

        // base Sikuli operations
        rmiFacade = new RmiFacade();
        rmiFacade.add("mouse", new DesktopMouse());
        rmiFacade.add("keyboard", new DesktopKeyboard());
        rmiFacade.add("desktop", new DesktopScreenRegion());
        try {
            rmiFacade.add("clipboard", Toolkit.getDefaultToolkit().getSystemClipboard());
        } catch (ExceptionInInitializerError e) {
            LOGGER.log(Level.SEVERE, "No System clipboard available", e);
        }

        // custom Sikuli operations
        rmiFacade.add("target-factory", new TargetFactory());
    }

    public MethodInvocationResultDto invoke(String objectId, MethodInvocationDto invocation) {
        return rmiFacade.invoke(objectId, invocation);
    }

}
