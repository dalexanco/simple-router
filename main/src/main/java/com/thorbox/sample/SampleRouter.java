package com.thorbox.sample;

import com.thorbox.simplerouter.annotation.AnnotationRouter;
import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.Router;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
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
    public void methodX(Request request, Response response, MatchContext context) {
        try {
            response.setCode(200);
            response.getPrintStream().println("Yeah !");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Route(path = "/test")
    public void methodB(Request request, Response response, MatchContext context) {
        try {
            response.setCode(200);
            response.getPrintStream().println("Test !");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Route(path = "/{id:integer}")
    public void methodC(Request request, Response response, MatchContext context) {
        try {
            response.setCode(200);
            response.getPrintStream().println("Loaded user #" + context.getRouteParams().get("id"));
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RouteNotFound
    public void methodNotFound(Request request, Response response, MatchContext context) {
        try {
            response.setCode(404);
            response.getPrintStream().println("Aucun user pour ce nom");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
