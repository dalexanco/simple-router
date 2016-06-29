package com.thorbox.simplerouter.core.route;

import com.google.code.regexp.Matcher;
import com.thorbox.simplerouter.core.HTTPNode;
import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.PathMatcher;

/**
 * Created by david on 21/06/16.
 */
public abstract class Routable extends HTTPNode {

    protected String path;
    private PathMatcher matcher;

    public Routable(String path) {
        super();
        setupPath(path);
    }

    protected void setupPath(String path) {
        this.path = path;
        this.matcher = new PathMatcher(path);
    }

    @Override
    public HTTPSession match(HTTPSession session) {
        Matcher matcher = this.matcher.executePattern(session.context.getRoute());
        boolean isMatching = matcher.matches();
        // If not matching, skip params and subpath extraction
        if(!isMatching) {
            MatchContext wrongContext = new MatchContext(false);
            HTTPSession wrongSession = new HTTPSession(session.request, session.response, wrongContext);
            return wrongSession;
        }
        // Prepare match context
        String subPath = this.matcher.subpath(matcher);
        session.context = new MatchContext(subPath, isMatching);
        // if found params, add them to context
        if(matcher.groupCount() > 1) {
            for(String paramKey : this.matcher.getParamKeys()) {
                String paramValue = matcher.group(paramKey);
                session.context.getRouteParams().put(paramKey, paramValue);
            }
        }
        return session;
    }

}
