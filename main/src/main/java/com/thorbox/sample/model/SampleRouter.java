package com.thorbox.sample.model;

import com.thorbox.simplerouter.annotation.model.Route;
import com.thorbox.simplerouter.annotation.model.RouteContainer;
import com.thorbox.simplerouter.annotation.model.RouteNotFound;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;

/**
 * Created by david on 05/02/2016.
 */
@RouteContainer(path = "/api/user")
public class SampleRouter {

    @Route(path = "/ok")
    public void methodX(Request request, Response response) {
        try {
            response.setCode(200);
            response.getPrintStream().println("Yeah !");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Route(path = "/test")
    public void methodB(Request request, Response response) {
        try {
            response.setCode(200);
            response.getPrintStream().println("Test !");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RouteNotFound
    public void methodNotFound(Request request, Response response) {
        try {
            response.setCode(404);
            response.getPrintStream().println("Aucun user pour ce nom");
            response.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
