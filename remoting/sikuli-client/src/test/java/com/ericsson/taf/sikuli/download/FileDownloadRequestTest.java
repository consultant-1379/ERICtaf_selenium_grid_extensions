package com.ericsson.taf.sikuli.download;

import com.ericsson.taf.sikuli.BaseRequestTest;
import com.ericsson.taf.sikuli.client.download.FileDownloadRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.seleniumhq.jetty9.server.AbstractNetworkConnector;
import org.seleniumhq.jetty9.server.Server;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 30/09/2015
 *         <p/>
 */
@RunWith(MockitoJUnitRunner.class)
public class FileDownloadRequestTest extends BaseRequestTest {

    private static final String SESSION_ID = "sessionId";
    private static final String EXPECTED_CONTENT = "expected content";

    @Mock
    Function responseHandleFunction;

    private File fileToGet;
    private Server server;
    private int port;

    @Before
    public void setUp() throws Exception {
        StubServlet stubServlet = new StubServlet();
        stubServlet.setFunction(responseHandleFunction);

        fileToGet = File.createTempFile("test", ".txt");
        FileUtils.write(fileToGet, EXPECTED_CONTENT);

        server = startServerForServlet(stubServlet, String.format(FileDownloadRequest.FILE_DOWNLOAD_EXTENSION_PATH, SESSION_ID, "*"));
        port = ((AbstractNetworkConnector) server.getConnectors()[0]).getLocalPort();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        assertTrue(fileToGet.delete());
    }

    @Test
    public void testDownload() throws IOException {
        doAnswer(verifyRequestContent())
                .when(responseHandleFunction)
                .apply(any(HttpServletRequest.class), any(HttpServletResponse.class));

        FileDownloadRequest fileDownloadRequest = new FileDownloadRequest("localhost", port, SESSION_ID);
        File downloadedFile = fileDownloadRequest.download(fileToGet.getAbsolutePath());

        try (FileInputStream fileInputStream = new FileInputStream(downloadedFile)) {
            String s = IOUtils.toString(fileInputStream);
            assertThat(s, is(EXPECTED_CONTENT));
        }
        verify(responseHandleFunction, times(1)).apply(any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    private Answer verifyRequestContent() {
        return new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                HttpServletRequest req = (HttpServletRequest) invocationOnMock.getArguments()[0];
                HttpServletResponse resp = (HttpServletResponse) invocationOnMock.getArguments()[1];

                String pathInfo = req.getPathInfo();
                assertThat(pathInfo, containsString(fileToGet.getAbsolutePath()));

                try (

                        FileInputStream fileInputStream = new FileInputStream(new File(pathInfo));
                        ServletOutputStream outputStream = resp.getOutputStream()) {
                    IOUtils.copy(fileInputStream, outputStream);
                }
                return null;
            }
        };
    }
}
