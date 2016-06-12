package com.thorbox.simplerouter.core.model;

import com.google.code.regexp.Matcher;
import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import com.thorbox.simplerouter.core.model.matcher.PathMatcher;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;


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
        if (!isMatching) {
            return new MatchContext(false);
        }
        // Prepare match context
        int groupCount = matcher.groupCount();
        String subPath = matcher.group((groupCount > 1) ? groupCount - 1 : 1);
        MatchContext context = new MatchContext(subPath, isMatching);
        // if found params, add them to context
        if (groupCount > 1) {
            for (String paramKey : this.matcher.getParamKeys()) {
                String paramValue = matcher.group(paramKey);
                context.getRouteParams().put(paramKey, paramValue);
            }
        }
        return context;
    }

    @Override
    protected void handleNotFound(Request request, Response response, MatchContext context) {
        if (notFoundRoute != null) {
            notFoundRoute.handle(request, response, context);
        } else {
            super.handleNotFound(request, response, context);
        }
    }

    public void setNotFoundRoute(RouteNodeModel notFoundRoute) {
        this.notFoundRoute = notFoundRoute;
    }
}
