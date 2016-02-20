package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * A routable is a combination of an object instance and a reference to a route method
 * Created by david on 07/02/2016.
 */
public class Routable {

    protected final Object instance;
    protected final Method instanceMethod;

    protected Routable(Object instance, Method instanceMethod) {
        this.instance = instance;
        this.instanceMethod = instanceMethod;
    }

    public static Routable from(Object instance, Method instanceMethod) {
        if(!isMethodLookLikeARoute(instanceMethod)) {
            String errorMessage = String.format(
                    "The method %s.%s does not look like a route handler (should take 3 parameters : Request, Response, MatchContext)",
                    instance.getClass().getSimpleName(),
                    instanceMethod.getName()
            );
            throw new IllegalAccessError(errorMessage);
        }
        return new Routable(instance, instanceMethod);
    }

    public void handle(Request request, Response response, MatchContext context) {
        try {
            instanceMethod.invoke(instance, request, response, context);
        } catch (Exception e) {
            sendSystemError(response);
        }
    }

    protected void sendSystemError(Response response) {
        if(!response.isCommitted()) {
            try {
                response.setCode(500);
                response.getPrintStream().println("Error : fail to invoke the given method");
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static boolean isMethodLookLikeARoute(Method method) {
        return method.getParameterCount() == 3 &&
                method.getParameterTypes()[0].equals(Request.class) &&
                method.getParameterTypes()[1].equals(Response.class) &&
                method.getParameterTypes()[2].equals(MatchContext.class);
    }

    public Object getInstance() {
        return instance;
    }

    public Method getInstanceMethod() {
        return instanceMethod;
    }
}
