package com.ericsson.taf.selenium.hub.proxy.session;

import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openqa.grid.internal.ExternalSessionKey;
import org.openqa.grid.internal.GridRegistry;
import org.openqa.grid.internal.TestSession;

import java.time.Clock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 22/09/2015
 *         <p/>
 */
@RunWith(MockitoJUnitRunner.class)
public class SeleniumSessionsTest {

    @Mock
    GridRegistry registry;
    @Mock
    ExternalSessionKey externalSessionKey;

    private TestSession session;

    @Before
    public void setUp() {
        session = spy(new TestSession(null, null, Clock.systemDefaultZone()));
        when(session.getExternalKey()).thenReturn(externalSessionKey);
        when(externalSessionKey.getKey()).thenReturn("key");
        when(registry.getActiveSessions()).thenReturn(Sets.newHashSet(session));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSessionIdExceptional() {
        SeleniumSessions.getSessionIdFromPath("/sessionId/");
    }

    @Test
    public void getSessionIdFromPath() {
        assertEquals("sessionId", SeleniumSessions.getSessionIdFromPath("/session/sessionId/"));
        assertEquals("sessionId", SeleniumSessions.getSessionIdFromPath("/session/sessionId/getCurrentWindow"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void trimSessionPathExceptional() {
        SeleniumSessions.trimSessionPath("/sessionId/");
    }

    @Test
    public void trimSessionPath() {
        assertEquals("", SeleniumSessions.trimSessionPath("/session/sessionId"));
        assertEquals("/request", SeleniumSessions.trimSessionPath("/session/id/request"));
        assertEquals("/another/one", SeleniumSessions.trimSessionPath("/session/id/another/one"));
    }

    @Test
    public void refreshShouldUpdateTimeoutTimer() throws InterruptedException {
        long inactivityTime = session.getInactivityTime();

        SeleniumSessions seleniumSessions = new SeleniumSessions(registry);
        seleniumSessions.refreshTimeout("key");

        long inactivityTimeAfterTouch = session.getInactivityTime();

        assertTrue(String.format("Timeout counter should be less than before: %d > %d", inactivityTime, inactivityTimeAfterTouch),
                inactivityTime > inactivityTimeAfterTouch);
    }

    @Test
    public void refreshShouldNotUpdateTimeoutIfIgnoreSet() throws InterruptedException {
        session.setIgnoreTimeout(true);

        long inactivityTime = session.getInactivityTime();

        SeleniumSessions seleniumSessions = new SeleniumSessions(registry);
        seleniumSessions.refreshTimeout("key");

        long inactivityTimeAfterTouch = session.getInactivityTime();

        assertTrue(inactivityTime == 0 && inactivityTimeAfterTouch == 0);
    }
}
