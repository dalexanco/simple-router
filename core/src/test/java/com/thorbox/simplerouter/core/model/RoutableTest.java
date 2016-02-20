package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.matcher.MatchContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

/**
 * Created by david on 11/02/2016.
 */
public class RoutableTest {

    private Request mockRequest;
    private Response mockResponse;

    private Dummy dummyObject;
    private Method routableMethod;
    private Method notRoutableMethod;
    private Method explodeMethod;

    private class Dummy {
        public void notRoutableMethod(String request, String params) {
        }

        public void routableMethod(Request request, Response response, MatchContext context) {
        }

        public void explodeMethod(Request request, Response response, MatchContext context) {
            throw new HeadlessException();
        }
    }

    @Before
    public void before() throws NoSuchMethodException, IOException {
        dummyObject = new Dummy();
        routableMethod = Dummy.class.getMethod("routableMethod", Request.class, Response.class, MatchContext.class);
        notRoutableMethod = Dummy.class.getMethod("notRoutableMethod", String.class, String.class);
        explodeMethod = Dummy.class.getMethod("explodeMethod", Request.class, Response.class, MatchContext.class);

        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
    }

    @Test(expected = IllegalAccessError.class)
    public void notRoutableMethod() {
        Routable.from(dummyObject, notRoutableMethod);
    }

    @Test
    public void routableMethod() {
        Routable routable = Routable.from(dummyObject, routableMethod);
        Assert.assertNotNull(routable);
    }

    @Test
    public void handleRoutableMethod() {
        Routable routable = spy(Routable.from(dummyObject, routableMethod));
        MatchContext matchContext = new MatchContext(true);
        routable.handle(mockRequest, mockResponse, matchContext);
        verify(routable, times(1)).handle(mockRequest, mockResponse, matchContext);
    }

    @Test
    public void handleFailRoutableMethod() {
        // Create a routable with it
        Routable routable = spy(Routable.from(dummyObject, explodeMethod));
        routable.handle(mockRequest, mockResponse, new MatchContext(true));
        verify(routable, times(1)).sendSystemError(mockResponse);
        verify(mockResponse).setCode(500);
    }

}
