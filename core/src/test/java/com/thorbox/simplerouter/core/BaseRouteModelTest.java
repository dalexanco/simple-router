package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.RouteRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;
/**
 * Created by david on 11/02/2016.
 */
public class BaseRouteModelTest {

    private Request mockRequest;
    private Response mockResponse;

    private BaseHTTPModel subjectRouteModel;

    @Before
    public void before() throws IOException, NoSuchMethodException {
        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();

        Method dummyRoutableMethod = DummyRouter.class.getMethod("dummyRoutableMethod", Request.class, Response.class, MatchContext.class);
        RouteRef routeRef = RouteRef.from(new DummyRouter(), dummyRoutableMethod);
        subjectRouteModel = spy(new BaseHTTPModel(routeRef));
    }

    @Test
    public void handleShouldCallRoutable() throws NoSuchMethodException {
        RouteRef mockRouteRef = Mockito.mock(RouteRef.class);
        when(mockRouteRef.getInstance()).thenReturn(new DummyRoutable());
        when(mockRouteRef.getInstanceMethod()).thenReturn(DummyRoutable.class.getMethod("dummy", Request.class, Response.class, MatchContext.class));
        subjectRouteModel = new BaseHTTPModel(mockRouteRef);
        HTTPNode router = new DummyRouter();
        router.add(subjectRouteModel);
        router.handle(mockRequest, mockResponse);
        verify(mockRouteRef, times(1)).handle(any(Request.class), any(Response.class), any(MatchContext.class));
    }

    private class DummyRoutable {
        public void dummy(Request req, Response resp, MatchContext context) {}
    }

    private class DummyRouter extends HTTPNode {

        public void dummyRoutableMethod(Request request, Response response, MatchContext context) {
        }

        @Override
        public MatchContext match(Request request, Response response, MatchContext currentPath) {
            return new MatchContext(true);
        }
    }
}
