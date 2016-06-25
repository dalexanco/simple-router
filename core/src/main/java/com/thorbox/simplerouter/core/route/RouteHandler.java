package com.thorbox.simplerouter.core.route;

import com.thorbox.simplerouter.core.model.HTTPSession;

/**
 * Created by david on 24/06/16.
 */
public class RouteHandler extends Routable {

    protected final RouteRef routeRef;

    public RouteHandler(String path, RouteRef routeRef) {
        super(path);
        this.routeRef = routeRef;
    }

    @Override
    public void handle(HTTPSession parentSession) {
        super.handle(parentSession);
        System.out.println("[handle] match with router " +
                routeRef.getInstance().getClass().getSimpleName() +
                "." + routeRef.getInstanceMethod().getName());
        routeRef.handle(parentSession);
    }

}
