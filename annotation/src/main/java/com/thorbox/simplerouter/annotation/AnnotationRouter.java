package com.thorbox.simplerouter.annotation;

import com.thorbox.simplerouter.core.*;
import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.RouteContainer;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.RouteRef;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.lang.reflect.Method;

/**
 * Created by david on 07/02/2016.
 */
public class AnnotationRouter extends HTTPNode {

    public void add(Object object) {
        RouteContainer routerAnnotation = object.getClass().getAnnotation(RouteContainer.class);
        PathHTTPContainer pathRouteContainer = new PathHTTPContainer(routerAnnotation.path());
        for(Method method : object.getClass().getMethods()) {
            RouteRef routeRef;
            Route routeAnnotation = method.getAnnotation(Route.class);
            if(routeAnnotation != null) {
                routeRef = RouteRef.from(object, method);
                pathRouteContainer.add(new PathHTTP(routeAnnotation.path(), routeAnnotation.method(), routeRef));
            } else if(method.getAnnotation(RouteNotFound.class) != null) {
                routeRef = RouteRef.from(object, method);
                pathRouteContainer.setNotFoundRoute(new BaseHTTPModel(routeRef));
            }
        }
        add(pathRouteContainer);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        return matchResult;
    }

}
