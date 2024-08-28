package com.ericsson.taf.rmi.protocol.client;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.23.
 */
public class RemoteNavigator {

    private final RemoteObjectProxyFactory proxyFactory;

    public RemoteNavigator(String host, int port, String path) {
        RestClient restClient = new RestClient(host, port, path);
        this.proxyFactory = new RemoteObjectProxyFactory(new RemoteInvoker(restClient));
    }

    public <T> T createProxy(Class<T> clazz, String objectId) {
        return proxyFactory.create(clazz, objectId);
    }
}
