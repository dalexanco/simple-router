package com.thorbox.simplerouter.core.route;

import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;

/**
 * Created by david on 21/06/16.
 */
public class Route extends RouteHandler {

    protected final String method;

    public Route(String path, String method, RouteRef routeRef) {
        super(path, routeRef);
        this.method = method;
    }

    @Override
    public HTTPSession match(HTTPSession session) {
        if(!session.request.getMethod().equals(method)) {
            session.context =new MatchContext(false);
            return session;
        }
        return super.match(session);
    }

    @Override
    protected void afterHandle(HTTPSession session) {
    }
}
