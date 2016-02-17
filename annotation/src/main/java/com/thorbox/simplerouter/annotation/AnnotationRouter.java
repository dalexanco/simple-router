package com.thorbox.simplerouter.annotation;

import com.thorbox.simplerouter.core.model.*;
import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.RouteContainer;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
import com.thorbox.simplerouter.core.model.BaseRouteModel;
import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.lang.reflect.Method;

/**
 * Created by david on 07/02/2016.
 */
public class AnnotationRouter extends RouteNodeModel {

    public void add(Object object) {
        RouteContainer routerAnnotation = object.getClass().getAnnotation(RouteContainer.class);
        PathRouteContainer pathRouteContainer = new PathRouteContainer(routerAnnotation.path());
        for(Method method : object.getClass().getMethods()) {
            Routable routable;
            Route routeAnnotation = method.getAnnotation(Route.class);
            if(routeAnnotation != null) {
                routable = Routable.from(object, method);
                pathRouteContainer.add(new PathRoute(routeAnnotation.path(), routeAnnotation.method(), routable));
            } else if(method.getAnnotation(RouteNotFound.class) != null) {
                routable = Routable.from(object, method);
                pathRouteContainer.setNotFoundRoute(new BaseRouteModel(routable));
            }
        }
        add(pathRouteContainer);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        return matchResult;
    }

}
