package com.thorbox.simplerouter.core.route;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

/**
 * Created by david on 11/02/2016.
 */
public class RouteRefTest {

    private Request mockRequest;
    private Response mockResponse;

    private Dummy dummyObject;
    private Method routableMethod;
    private Method notRoutableMethod;
    private Method explodeMethod;

    private class Dummy {
        public void notRoutableMethod(String request, String params) {
        }

        public HTTPSession routableMethod(HTTPSession session) {
            return session;
        }

        public HTTPSession explodeMethod(HTTPSession session) {
            throw new HeadlessException();
        }
    }

    @Before
    public void before() throws NoSuchMethodException, IOException {
        dummyObject = new Dummy();
        routableMethod = Dummy.class.getMethod("routableMethod", HTTPSession.class);
        notRoutableMethod = Dummy.class.getMethod("notRoutableMethod", String.class, String.class);
        explodeMethod = Dummy.class.getMethod("explodeMethod", HTTPSession.class);

        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
    }

    @Test(expected = IllegalAccessError.class)
    public void notRoutableMethod() {
        RouteRef.from(dummyObject, notRoutableMethod);
    }

    @Test
    public void routableMethod() {
        RouteRef routeRef = RouteRef.from(dummyObject, routableMethod);
        Assert.assertNotNull(routeRef);
    }

    @Test
    public void handleRoutableMethod() {
        RouteRef routeRef = spy(RouteRef.from(dummyObject, routableMethod));
        MatchContext matchContext = new MatchContext(true);
        HTTPSession session = new HTTPSession(mockRequest, mockResponse, matchContext);
        routeRef.handle(session);
        verify(routeRef, times(1)).handle(session);
    }

    @Test
    public void handleFailRoutableMethod() {
        // Create a routeRef with it
        RouteRef routeRef = spy(RouteRef.from(dummyObject, explodeMethod));
        routeRef.handle(new HTTPSession(mockRequest, mockResponse, new MatchContext(true)));
        verify(routeRef, times(1)).sendSystemError(mockResponse);
        verify(mockResponse).setCode(500);
    }

}
