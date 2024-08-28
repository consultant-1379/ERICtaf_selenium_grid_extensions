package com.ericsson.taf.rmi.protocol.server;


import com.ericsson.taf.rmi.protocol.MethodInvocationResultDto;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Mihails Volkovs <mihails.volkovs@ericsson.com>
 * 2015.09.24.
 */
class Marshaller {

    private static final Set<Class<?>> SIMPLE_OBJECT_CLASSES = new HashSet<>();

    static {
        SIMPLE_OBJECT_CLASSES.add(Boolean.class);

        SIMPLE_OBJECT_CLASSES.add(Byte.class);
        SIMPLE_OBJECT_CLASSES.add(Short.class);
        SIMPLE_OBJECT_CLASSES.add(Integer.class);

        SIMPLE_OBJECT_CLASSES.add(Long.class);
        SIMPLE_OBJECT_CLASSES.add(Float.class);
        SIMPLE_OBJECT_CLASSES.add(Double.class);

        SIMPLE_OBJECT_CLASSES.add(Character.class);
        SIMPLE_OBJECT_CLASSES.add(String.class);
    }

    private ObjectLocator objectLocator;

    public Marshaller(ObjectLocator objectLocator) {
        this.objectLocator = objectLocator;
    }

    public MethodInvocationResultDto toResponse(Object newObject, Class<?> returnType) {
        if (newObject == null) {
            String publicReturnType = getReturnTypeOrNullIfVoid(returnType);
            return new MethodInvocationResultDto(null, publicReturnType);
        }

        Class<?> resultClass = newObject.getClass();
        if (SIMPLE_OBJECT_CLASSES.contains(resultClass)) {
            return new MethodInvocationResultDto(newObject.toString(), newObject.getClass().getName());
        }

        String newWidgetId = objectLocator.put(newObject);
        String newWidgetType = ClassUtils.getFirstPublicType(resultClass).getName();
        return new MethodInvocationResultDto(newWidgetId, newWidgetType);
    }

    private String getReturnTypeOrNullIfVoid(Class<?> returnType) {
        return void.class.equals(returnType) || Void.class.equals(returnType) ? null :
                ClassUtils.getFirstPublicType(returnType).getName();
    }
}
