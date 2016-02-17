package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import com.thorbox.simplerouter.core.model.matcher.PathMatcher;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.util.regex.Matcher;

/**
 * A PathRoute is a Route defined by a method and a path
 * The @path can contains route parameters
 * Created by david on 09/02/2016.
 */
public class PathRoute extends BaseRouteModel {

    protected final String path;
    protected final String method;
    private PathMatcher matcher;

    public PathRoute(String path, String method, Routable routable) {
        super(routable);
        this.path = path;
        this.method = method;
        this.matcher = new PathMatcher(path);
    }

    @Override
    public MatchContext match(Request request, Response response, MatchContext matchResult) {
        if(!request.getMethod().equals(method)) {
            return new MatchContext(false);
        }
        Matcher matcher = this.matcher.executePattern(request.getPath().getPath());
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
}
