package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.RouteRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by david on 15/02/2016.
 */
public class PathRouteTest {

    private Request mockRequest;
    private Response mockResponse;

    private PathHTTP subjectSimpleRoute;
    private PathHTTP subjectParamsRoute;

    @Before
    public void before() throws NoSuchMethodException, IOException {
        Method method = TestRoutable.class.getMethod("test", Request.class, Response.class, MatchContext.class);
        RouteRef routeRef = new RouteRef(new TestRoutable(), method);
        subjectSimpleRoute = spy(new PathHTTP("/test/v1", "GET", routeRef));
        subjectParamsRoute = spy(new PathHTTP("/category/{category}", "GET", routeRef));

        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
    }

    @Test
    public void handleSimpleRoute() {
        when(mockRequest.getMethod()).thenReturn("GET");
        testHandleRoute(subjectSimpleRoute, "/test/v1", true);
    }

    @Test
    public void handleParamsRoute() {
        when(mockRequest.getMethod()).thenReturn("GET");
        MatchContext matchContext = testHandleRoute(subjectParamsRoute, "/category/dog", true);
        assertEquals(1, matchContext.getRouteParams().keySet().size());
        assertTrue(matchContext.getRouteParams().containsKey("category"));
        assertEquals("dog", matchContext.getRouteParams().get("category"));
    }

    @Test
    public void skipBadMethod() {
        when(mockRequest.getMethod()).thenReturn("DELETE");
        testHandleRoute(subjectSimpleRoute, "/test/v1", false);
    }

    private MatchContext testHandleRoute(PathHTTP subject, String requestPath, boolean shouleBeHandled) {
        when(mockRequest.getPath()).thenReturn(new PathParser(requestPath));
        new TestContainer(subject).handle(mockRequest, mockResponse);
        ArgumentCaptor<MatchContext> argument = ArgumentCaptor.forClass(MatchContext.class);
        verify(subject, times(shouleBeHandled ? 1 : 0)).handle(any(Request.class), any(Response.class), argument.capture());
        if(shouleBeHandled) {
            assertEquals(shouleBeHandled, argument.getValue().isMatching());
            return argument.getValue();
        }
        return null;
    }

    public class TestRoutable {
        public void test(Request request, Response response, MatchContext context) {

        }
    }

    public class TestContainer extends HTTPNode {
        public TestContainer(HTTPNode subject) {
            super();
            add(subject);
        }

        @Override
        public MatchContext match(Request request, Response response, MatchContext parentMatch) {
            return new MatchContext(request.getPath().getPath(), true);
        }
    }
}
