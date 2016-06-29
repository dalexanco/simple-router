package com.thorbox.sample;

import com.thorbox.simplerouter.annotation.AnnotationRouter;
import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.Router;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;

/**
 * Created by david on 05/02/2016.
 */
@Router(path = "/api/user")
public class SampleRouter extends AnnotationRouter {

    @Route(path = "/ok")
    public void methodX(HTTPSession session) {
        try {
            session.response.setCode(200);
            session.response.getPrintStream().println("Yeah !");
            session.response.close();
            session.response.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Route(path = "/test")
    public void methodB(HTTPSession session) {
        try {
            session.response.setCode(200);
            session.response.getPrintStream().println("Test !");
            session.response.close();
            session.response.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Route(path = "/{id:integer}")
    public void methodC(HTTPSession session) {
        try {
            session.response.setCode(200);
            session.response.getPrintStream().println("Loaded user #" + session.context.getRouteParams().get("id"));
            session.response.close();
            session.response.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RouteNotFound
    public void methodNotFound(HTTPSession session) {
        try {
            session.response.setCode(404);
            session.response.getPrintStream().println("Aucun user pour ce nom");
            session.response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
