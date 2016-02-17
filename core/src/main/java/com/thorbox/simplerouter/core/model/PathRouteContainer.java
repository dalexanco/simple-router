package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import com.thorbox.simplerouter.core.model.matcher.PathMatcher;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.util.regex.Matcher;

/**
 * Created by david on 09/02/2016.
 */
public class PathRouteContainer extends RouteNodeModel {

    protected final String path;
    protected final PathMatcher matcher;

    protected RouteNodeModel notFoundRoute;

    public PathRouteContainer(String path) {
        this.path = path;
        this.matcher = new PathMatcher(path);
    }

    @Override
    public void handle(Request request, Response response, MatchContext matchResult) {
        System.out.println("[handle] Process request with router " + this.getClass().getSimpleName());
        super.handle(request, response, matchResult);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        // Run regex on this path
        Matcher matcher = this.matcher.executePattern(matchResult.getRoute());
        boolean isMatching = matcher.matches();
        // If not matching, skip params and subpath extraction
        if(!isMatching) {
            return new MatchContext(false);
        }
        // Prepare match context
        String subPath = matcher.group(matcher.groupCount()-1);
        MatchContext context = new MatchContext(subPath, isMatching);
        // if found params, add them to context
        if(matcher.groupCount() > 1) {
            for(String paramKey : this.matcher.getParamKeys()) {
                String paramValue = matcher.group(paramKey);
                context.getRouteParams().put(paramKey, paramValue);
            }
        }
        return context;
    }

    @Override
    protected void handleNotFound(Request request, Response response) {
        if (notFoundRoute != null) {
            notFoundRoute.handle(request, response);
        } else {
            super.handleNotFound(request, response);
        }
    }

    public void setNotFoundRoute(RouteNodeModel notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }
}
