package com.ericsson.taf.selenium.hub.proxy.session;

import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;

import java.net.URL;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 21/09/2015
 */
public class SeleniumSessions {

    private static final Logger LOGGER = Logger.getLogger(SeleniumSessions.class.getName());
    private static final Pattern SESSION_ID_PATTERN = Pattern.compile("/session/([^/]+).*");

    private final GridRegistry registry;

    public SeleniumSessions(GridRegistry registry) {
        this.registry = registry;
    }

    public URL getRemoteHostForSession(String sessionId) {
        for (TestSession activeSession : registry.getActiveSessions()) {
            if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                return activeSession.getSlot().getProxy().getRemoteHost();
            }
        }
        throw new IllegalArgumentException("Invalid sessionId. No active session is present for id:" + sessionId);
    }

    public void refreshTimeout(String sessionId) {
        for (TestSession activeSession : registry.getActiveSessions()) {

            ExternalSessionKey externalSessionKey = activeSession.getExternalKey();
            if (externalSessionKey == null) {
                LOGGER.warning("External session key is null. " +
                        "Could not refreshTimeout of session with internal id of " + activeSession.toString());
            } else if (sessionId.equals(activeSession.getExternalKey().getKey())) {
                refreshTimeout(activeSession);
            }
        }
    }

    private void refreshTimeout(TestSession activeSession) {
        if (activeSession.getInactivityTime() != 0) {
            activeSession.setIgnoreTimeout(false);
        }
    }

    public static String getSessionIdFromPath(String pathInfo) {
        Matcher matcher = SESSION_ID_PATTERN.matcher(pathInfo);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Invalid request. Session Id is not present");
    }

    public static String trimSessionPath(String pathInfo) {
        return pathInfo.replaceFirst("/session/" + getSessionIdFromPath(pathInfo), "");
    }

}
