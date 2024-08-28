package com.ericsson.taf.selenium.hub.proxy.client;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 22/09/2015
 *         <p/>
 */
public class RequestForwardingClientProvider {
    public RequestForwardingClient provide(String host, int port) {
        return new RequestForwardingClient(host, port);
    }
}
