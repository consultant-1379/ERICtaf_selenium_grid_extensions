package com.ericsson.taf.rmi.protocol.server;

import com.ericsson.taf.rmi.protocol.MethodInvocationDto;
import com.ericsson.taf.rmi.protocol.MethodInvocationResultDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
public class RmiFacade {

    private static final Logger LOG = LoggerFactory.getLogger(RmiFacade.class);

    private ObjectLocator objectLocator = new ObjectLocator();

    private Marshaller marshaller = new Marshaller(objectLocator);

    private MethodInvoker invoker = new MethodInvoker(objectLocator);

    public MethodInvocationResultDto invoke(String objectId, MethodInvocationDto invocation) {

        // object location
        Object object = objectLocator.get(objectId);
        if (object == null) {
            LOG.error("Object ({}) not found. Please make sure objects cache size (for objects chained invocation).", objectId);
        }

        // method invocation
        Object result;
        Class<?> returnType = null;
        try {
            MethodInvoker.InvocationResult invocationResult = invoker.invoke(object, invocation);
            result = invocationResult.getResult();
            returnType = invocationResult.getResultClass();
        } catch (RuntimeException e) {
            throw new RemoteMethodInvocationException(e.getMessage(), e);
        }

        // serializing invocation result
        return marshaller.toResponse(result, returnType);
    }

    public void add(String objectId, Object object) {
        objectLocator.addPermanentObject(objectId, object);
    }

}
