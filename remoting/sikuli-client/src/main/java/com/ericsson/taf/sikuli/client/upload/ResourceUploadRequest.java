package com.ericsson.taf.sikuli.client.upload;

import com.google.common.base.Throwables;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.commons.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 25/09/2015
 *         <p/>
 */
public class ResourceUploadRequest {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ResourceUploadRequest.class);

    public static final String FILE_UPLOAD_EXTENSION_PATH = "/grid/admin/HubRequestsProxyingServlet/session/%s/FileUploadServlet";

    private final HttpHost httpHost;
    private final String sessionId;

    public ResourceUploadRequest(String host, int port, String sessionId) {
        this.httpHost = new HttpHost(host, port);
        this.sessionId = sessionId;
    }

    public String upload(String resourcesPath) {
        File zip = addResourcesToZip(resourcesPath);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost request = new HttpPost(String.format(FILE_UPLOAD_EXTENSION_PATH, sessionId));
        request.setHeader("Content-Type", "application/octet-stream");

        try {
            FileInputStream fileInputStream = new FileInputStream(zip);
            InputStreamEntity entity = new InputStreamEntity(fileInputStream);
            request.setEntity(entity);

            CloseableHttpResponse execute = httpClient.execute(httpHost, request);

            int statusCode = execute.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK == statusCode) {
                InputStream response = execute.getEntity().getContent();
                return IOUtils.toString(response, "utf-8");
            } else {
                throw new ResourceUploadException("Upload response returned status " + statusCode);
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    protected File addResourcesToZip(String resourcesPath) {
        ResourceFolder resourceFolder = new ResourceFolder(resourcesPath);
        return resourceFolder.toZip();
    }
}
