package com.thorbox.sample.model;

/**
 * Created by david on 05/02/2016.
 *
@SimpleRouter(basepath = "/api/user")
public class SampleFilteredRouter {

    @Filter(name = MyFilter.class)
    @SimpleRoute(method = POST, route = "{user_id}")
    public void methodX(Request request, Response response) {

    }

    public class MyFilter implements SimpleRouteFilter {

        @Override
        public boolean filter(Request request, Response response) {
            return false;
        }
    }

    public interface SimpleRouteFilter {
        boolean filter(Request request, Response response);
    }
}
*/