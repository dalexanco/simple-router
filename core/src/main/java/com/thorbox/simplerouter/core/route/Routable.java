package com.thorbox.simplerouter.core.route;

import com.google.code.regexp.Matcher;
import com.thorbox.simplerouter.core.HTTPNode;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.PathMatcher;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 * Created by david on 21/06/16.
 */
public abstract class Routable extends HTTPNode {

    protected final String path;
    private PathMatcher matcher;

    public Routable(String path) {
        super();
        this.path = path;
        this.matcher = new PathMatcher(path);
    }

    public MatchContext match(Request request, Response response, MatchContext matchResult) {
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
}
