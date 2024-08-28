package com.ericsson.taf.selenium.hub.proxy.client;

/**
 * @author Alexey Nikolaenko alexey.nikolaenko@ericsson.com
 *         Date: 22/09/2015
 *         <p/>
 */
public class UnsupportedHttpMethodException extends RuntimeException {
    public UnsupportedHttpMethodException(String method) {
        super(String.format("Method %s is not supported", method));
    }
}
