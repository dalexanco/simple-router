package com.thorbox.simplerouter.core.route;

import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.RouteRef;
import com.thorbox.simplerouter.core.model.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 * Created by david on 21/06/16.
 */
public class Route extends Routable {

    protected final RouteRef routeRef;
    protected final String method;

    public Route(String path, String method, RouteRef routeRef) {
        super(path);
        this.routeRef = routeRef;
        this.method = method;
    }

    @Override
    public void handle(HTTPSession parentSession) {
        super.handle(parentSession);
        System.out.println("[handle] match with router " +
                routeRef.getInstance().getClass().getSimpleName() +
                "." + routeRef.getInstanceMethod().getName());
        routeRef.handle(parentSession);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        if(!request.getMethod().equals(method)) {
            return new MatchContext(false);
        }
        return super.match(request, response, matchResult);
    }
}
