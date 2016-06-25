package com.thorbox.simplerouter.core.route;

import com.thorbox.simplerouter.core.HTTPNode;
import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
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
public class RouteTest {

    private Request mockRequest;
    private Response mockResponse;

    private Route subjectSimpleRoute;
    private Route subjectParamsRoute;

    @Before
    public void before() throws NoSuchMethodException, IOException {
        Method method = TestRoutable.class.getMethod("test", Request.class, Response.class, MatchContext.class);
        RouteRef routeRef = new RouteRef(new TestRoutable(), method);
        subjectSimpleRoute = spy(new Route("/test/v1", "GET", routeRef));
        subjectParamsRoute = spy(new Route("/category/{category}", "GET", routeRef));

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
        HTTPSession session = testHandleRoute(subjectParamsRoute, "/category/dog", true);
        assertEquals(1, session.context.getRouteParams().keySet().size());
        assertTrue(session.context.getRouteParams().containsKey("category"));
        assertEquals("dog", session.context.getRouteParams().get("category"));
    }

    @Test
    public void skipBadMethod() {
        when(mockRequest.getMethod()).thenReturn("DELETE");
        testHandleRoute(subjectSimpleRoute, "/test/v1", false);
    }

    private HTTPSession testHandleRoute(Route subject, String requestPath, boolean shouleBeHandled) {
        when(mockRequest.getPath()).thenReturn(new PathParser(requestPath));
        new TestContainer(subject).handle(mockRequest, mockResponse);
        ArgumentCaptor<HTTPSession> argument = ArgumentCaptor.forClass(HTTPSession.class);
        verify(subject, times(shouleBeHandled ? 1 : 0)).handle(argument.capture());
        if(shouleBeHandled) {
            assertEquals(shouleBeHandled, argument.getValue().context.isMatching());
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
        public HTTPSession match(HTTPSession session) {
            session.context = new MatchContext(session.request.getPath().getPath(), true);
            return session;
        }
    }
}
