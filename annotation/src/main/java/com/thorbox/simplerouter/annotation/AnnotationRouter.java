package com.thorbox.simplerouter.annotation;

import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.Router;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
import com.thorbox.simplerouter.core.HTTPNode;
import com.thorbox.simplerouter.core.route.RouteHandler;
import com.thorbox.simplerouter.core.route.RouteRef;

import java.lang.reflect.Method;

/**
 * Created by david on 07/02/2016.
 */
public class AnnotationRouter extends com.thorbox.simplerouter.core.route.Router {

    public AnnotationRouter(String path) {
        super(path);

        // Look for routes and add as new RouteNodes
        for(Method method : getClass().getMethods()) {
            RouteRef routeRef;
            Route routeAnnotation = method.getAnnotation(Route.class);
            if(routeAnnotation != null) {
                routeRef = RouteRef.from(this, method);
                this.add(new com.thorbox.simplerouter.core.route.Route(routeAnnotation.path(), routeAnnotation.method(), routeRef));
            } else if(method.getAnnotation(RouteNotFound.class) != null) {
                routeRef = RouteRef.from(this, method);
                this.setNotFoundNode(new RouteHandler("", routeRef));
            }
        }
    }

    public AnnotationRouter() {
        this("");

        // If found a Router annotation, use it as main path
        Router routerAnnotation = getClass().getAnnotation(Router.class);
        if(routerAnnotation != null) {
            this.setupPath(routerAnnotation.path());
        }
    }


}
