package com.thorbox.simplerouter.core.model;

import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

/**
 * Created by david on 21/06/16.
 */
public class HTTPSession {

    public MatchContext context;
    public Request request;
    public Response response;

    public HTTPSession(Request request, Response response, MatchContext context) {
        this.request = request;
        this.response = response;
        this.context = context;
    }
}
