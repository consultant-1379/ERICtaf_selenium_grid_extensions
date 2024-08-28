package com.ericsson.taf.selenium.node.upload;


import com.ericsson.taf.selenium.node.BaseServletITest;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.seleniumhq.jetty9.server.AbstractNetworkConnector;
import org.seleniumhq.jetty9.server.Server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Alexey Nikolaenko alexey@tcherezov.com
 *         Date: 24/09/2015
 */
public class FileUploadServletITest extends BaseServletITest {

    private int port;
    private File zipArchive;
    private Server fileUploadServer;
    private File unzippedFile;
    private File unzippedArchive;

    @Before
    public void setUp() throws Exception {
        fileUploadServer = startServerForServlet(new FileUploadServlet(), "/" + FileUploadServlet.class.getSimpleName() + "/*");
        port = ((AbstractNetworkConnector) fileUploadServer.getConnectors()[0]).getLocalPort();

        zipArchive = createZipArchiveWithTextFile();
    }

    @Test
    public void testDoPost() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://localhost:" + port + "/FileUploadServlet/");
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.OCTET_STREAM.toString());

        FileInputStream fileInputStream = new FileInputStream(zipArchive);
        InputStreamEntity entity = new InputStreamEntity(fileInputStream);
        httpPost.setEntity(entity);

        CloseableHttpResponse execute = httpClient.execute(httpPost);

        StatusLine statusLine = execute.getStatusLine();
        assertThat(statusLine.getStatusCode(), equalTo(200));

        try (
                InputStream content = execute.getEntity().getContent()) {
            String directory = IOUtils.toString(content, StandardCharsets.UTF_8);
            unzippedArchive = new File(directory);
            unzippedFile = new File(directory + "/" + ZIP_FILE_NAME);
        }

        assertThat(unzippedFile.exists(), is(true));

        try (FileInputStream unzippedFileStream = new FileInputStream(unzippedFile)) {
            String contents = IOUtils.toString(unzippedFileStream, StandardCharsets.UTF_8);
            assertThat(contents, is("test data"));
        }
    }

    @After
    public void tearDown() throws Exception {
        deleteIfExists(unzippedFile, unzippedArchive, zipArchive);
        fileUploadServer.stop();
    }
}

