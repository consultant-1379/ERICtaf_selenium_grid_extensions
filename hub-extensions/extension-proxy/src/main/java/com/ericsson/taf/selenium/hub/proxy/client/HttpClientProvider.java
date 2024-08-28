package com.ericsson.taf.selenium.hub.proxy.client;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 21/09/2015
 *         <p/>
 */
public class HttpClientProvider {

    public CloseableHttpClient provide() {
        return HttpClients.createDefault();
    }
}
