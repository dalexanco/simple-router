package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
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
public abstract class HTTPNode implements Container {

    protected List<HTTPNode> subs;
    protected HTTPNode notFoundNode = new DefaultNotFoundNode();

    public HTTPNode() {
        this.subs = new ArrayList<>();
    }

    public void add(HTTPNode route) {
        subs.add(route);
    }

    public void handle(HTTPSession parentSession) {
        // Check for sub nodes
        for (HTTPNode route : subs) {
            HTTPSession currentSession = route.match(parentSession);
            if (currentSession.context.isMatching()) {
                route.handle(currentSession);
                return;
            }
        }
        // Try to call not found
        if(notFoundNode != null) {
            notFoundNode.handle(parentSession);
        }
    }

    private class DefaultNotFoundNode extends HTTPNode {
        @Override
        public void handle(HTTPSession session) {
            super.handle(session);
            try {
                session.response.setCode(404);
                session.response.getPrintStream().println("Not found");
                session.response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Match check if the given request can be handled be the current HTTPNode, the current path
     * to handle is stored in parentMatch
     * WARNING : parentMatch contains previous parameters extracted from parents HTTPNode, you have
     * to transfers these params if return a new instance of MatchContext
     * @param session Contains request, response and match context
     * @return
     */
    public HTTPSession match(HTTPSession session) {
        return session;
    }

    // Container Interface //

    public void handle(Request request, Response response) {
        MatchContext rootMatchResult = new MatchContext(request.getPath().getPath(), true);
        this.handle(new HTTPSession(request, response, rootMatchResult));
    }
}
