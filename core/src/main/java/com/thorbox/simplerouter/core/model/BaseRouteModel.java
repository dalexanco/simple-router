package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 * Created by david on 09/02/2016.
 */
public class BaseRouteModel extends RouteNodeModel {

    protected final Routable routable;

    public BaseRouteModel(Routable routable) {
        this.routable = routable;
    }

    @Override
    public void handle(Request request, Response response, MatchContext matchResult) {
        System.out.println("[handle] match with router " +
                routable.getInstance().getClass().getSimpleName() +
                "." + routable.getInstanceMethod().getName());
        routable.handle(request, response, matchResult);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        return matchResult;
    }
}
