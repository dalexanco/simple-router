package com.thorbox.simplerouter.core.model;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * A routeRef is a combination of an object instance and a reference to a route method
 * Created by david on 07/02/2016.
 */
public class RouteRef {

    protected final Object instance;
    protected final Method instanceMethod;

    protected RouteRef(Object instance, Method instanceMethod) {
        this.instance = instance;
        this.instanceMethod = instanceMethod;
    }

    public static RouteRef from(Object instance, Method instanceMethod) {
        if (!isMethodLookLikeARoute(instanceMethod)) {
            String errorMessage = String.format(
                    "The method %s.%s does not look like a route handler (should take 3 parameters : Request, Response, MatchContext)",
                    new Object[]{instance.getClass().getSimpleName(), instanceMethod.getName()}
            );
            throw new IllegalAccessError(errorMessage);
        }
        return new RouteRef(instance, instanceMethod);
    }

    public void handle(HTTPSession session) {
        try {
            instanceMethod.invoke(instance, new Object[]{session});
        } catch (Exception e) {
            // TODO : error manager (enable json error for example)
            sendSystemError(session.response);
        }
    }

    protected void sendSystemError(Response response) {
        if (!response.isCommitted()) {
            try {
                response.setCode(500);
                response.getPrintStream().println("Error : fail to invoke the given method");
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A route method takes an HTTPSession as unique parameter
     * @param method
     * @return
     */
    private static boolean isMethodLookLikeARoute(Method method) {
        Class[] parameterTypes = method.getParameterTypes();
        Class returnType = method.getReturnType();
        return parameterTypes.length == 1 &&
                parameterTypes[0].equals(HTTPSession.class) &&
                returnType.equals(Void.class);
    }

    public Object getInstance() {
        return instance;
    }

    public Method getInstanceMethod() {
        return instanceMethod;
    }
}