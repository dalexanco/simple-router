package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to define a routing graph
 * Created by david on 09/02/2016.
 */
public abstract class RouteNodeModel implements Container {

    protected List<RouteNodeModel> subs;

    public RouteNodeModel() {
        this.subs = new ArrayList<>();
    }

    public void add(RouteNodeModel route) {
        subs.add(route);
    }

    public void handle(Request request, Response response, MatchContext currentMatch) {
        for (RouteNodeModel route : subs) {
            MatchContext matchResult = route.match(request, response, currentMatch);
            if (matchResult.isMatching()) {
                route.handle(request, response, matchResult);
                return;
            }
        }
        handleNotFound(request, response);
    }

    protected void handleNotFound(Request request, Response response) {
        try {
            response.setCode(404);
            response.getPrintStream().println("Not found");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<RouteNodeModel> getSubs() {
        return subs;
    }

    // Abstract methods //

    /**
     * Match check if the given request can be handled be the current RouteNodeModel, the current path
     * to handle is stored in parentMatch
     * WARNING : parentMatch contains previous parameters extracted from parents RouteNodeModel, you have
     * to transfers these params if return a new instance of MatchContext
     * @param request The original http request
     * @param response The response to send
     * @param parentMatch The match context from previous node
     * @return
     */
    public abstract MatchContext match(Request request, Response response, MatchContext parentMatch);

    // Container Interface //

    public void handle(Request request, Response response) {
        MatchContext rootMatchResult = new MatchContext(request.getPath().getPath(), true);
        this.handle(request, response, rootMatchResult);
    }
}
