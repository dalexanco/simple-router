package com.thorbox.simplerouter.core.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by david on 12/02/2016.
 */
public class MatchContext {

    protected final String route;
    protected final boolean isMatching;
    private final Map<String, String> routeParams;

    public MatchContext(String currentRoute, boolean match) {
        this.route = currentRoute;
        this.isMatching = match;
        this.routeParams = new HashMap<>();
    }

    public MatchContext(boolean isMatching) {
        this("", isMatching);
    }

    public boolean isMatching() {
        return isMatching;
    }

    public Map<String, String> getRouteParams() {
        return routeParams;
    }

    public String getRoute() {
        return route;
    }
}
