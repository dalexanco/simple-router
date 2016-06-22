package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.model.RouteRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by david on 12/02/2016.
 */
public class PathRouteContainerTest {

    private Request mockRequest;
    private Response mockResponse;

    private PathHTTPContainer subjectSimpleRouter;
    private PathHTTPContainer subjectParamsRouter;
    private PathHTTPContainer subjectIntParamsRouter;
    private PathHTTPContainer subjectMultiParamsRouter;
    private HTTPNode simpleContainer;
    private HTTPNode paramsContainer;
    private HTTPNode intParamsContainer;
    private HTTPNode multiParamsContainer;

    @Before
    public void before() throws IOException {
        subjectSimpleRouter = spy(new PathHTTPContainer("/test"));
        subjectParamsRouter = spy(new PathHTTPContainer("/dummy/list/{id}"));
        subjectIntParamsRouter = spy(new PathHTTPContainer("/dummy/list/{id:integer}"));
        subjectMultiParamsRouter = spy(new PathHTTPContainer("/category/{category}/details/{id}"));

        // Prepare simple router
        simpleContainer = new HTTPNode() {
            @Override
            public MatchContext match(Request request, Response response, MatchContext parentMatch) {
                return new MatchContext(request.getPath().getPath(), true);
            }
        };
        simpleContainer.add(subjectSimpleRouter);

        // Prepare params router
        paramsContainer = new HTTPNode() {
            @Override
            public MatchContext match(Request request, Response response, MatchContext parentMatch) {
                return new MatchContext(request.getPath().getPath(), true);
            }
        };
        paramsContainer.add(subjectParamsRouter);

        // Prepare int params router
        intParamsContainer = new HTTPNode() {
            @Override
            public MatchContext match(Request request, Response response, MatchContext parentMatch) {
                return new MatchContext(request.getPath().getPath(), true);
            }
        };
        intParamsContainer.add(subjectIntParamsRouter);

        // Prepare multi-params router
        multiParamsContainer = new HTTPNode() {
            @Override
            public MatchContext match(Request request, Response response, MatchContext parentMatch) {
                return new MatchContext(request.getPath().getPath(), true);
            }
        };
        multiParamsContainer.add(subjectMultiParamsRouter);

        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
    }

    @Test
    public void shouldAcceptAllWhenUsedAsContainer() {
        testHandleRoute(subjectSimpleRouter, subjectSimpleRouter, "/blabla", true);
    }

    @Test
    public void shouldHandleSimpleRoute() {
        testHandleRoute(subjectSimpleRouter, simpleContainer, "/test/category/item/1234", true);
    }

    @Test
    public void handleExactRoute() {
        testHandleRoute(subjectSimpleRouter, simpleContainer, "/test/", true);
    }

    @Test
    public void handleExactRouteWithoutSeparator() {
        testHandleRoute(subjectSimpleRouter, simpleContainer, "/test", true);
    }

    @Test
    public void skipBadRoute() {
        testHandleRoute(subjectSimpleRouter, simpleContainer, "/blabla", false);
    }

    @Test
    public void handleStartWithRoute() {
        testHandleRoute(subjectSimpleRouter, simpleContainer, "/test123", true);
    }

    @Test
    public void shouldHandleRouteWithParameters() {
        testHandleRoute(subjectParamsRouter, paramsContainer, "/dummy/list/123", true);
    }

    @Test
    public void shouldHandleRouteWithTypedParameters() {
        testHandleRoute(subjectIntParamsRouter, intParamsContainer, "/dummy/list/123", true);
    }

    @Test
    public void skipBadTypeParameter() {
        testHandleRoute(subjectIntParamsRouter, intParamsContainer, "/dummy/list/abc", false);
    }

    @Test
    public void shouldExtractOneParameter() {
        when(mockRequest.getPath()).thenReturn(new PathParser("/dummy/list/123"));
        paramsContainer.handle(mockRequest, mockResponse);

        ArgumentCaptor<MatchContext> argument = ArgumentCaptor.forClass(MatchContext.class);
        verify(subjectParamsRouter).handle(any(Request.class), any(Response.class), argument.capture());

        Map<String, String> extractedParams = argument.getValue().getRouteParams();
        assertTrue(extractedParams.containsKey("id"));
        assertEquals("123", extractedParams.get("id"));
    }

    @Test
    public void shouldExtractManyParameters() {
        when(mockRequest.getPath()).thenReturn(new PathParser("/category/tips/details/123"));
        multiParamsContainer.handle(mockRequest, mockResponse);

        ArgumentCaptor<MatchContext> argument = ArgumentCaptor.forClass(MatchContext.class);
        verify(subjectMultiParamsRouter).handle(any(Request.class), any(Response.class), argument.capture());

        Map<String, String> extractedParams = argument.getValue().getRouteParams();
        assertTrue(extractedParams.containsKey("category"));
        assertEquals("tips", extractedParams.get("category"));
        assertTrue(extractedParams.containsKey("id"));
        assertEquals("123", extractedParams.get("id"));
    }

    @Test
    public void shouldCallNoRouteFound() throws NoSuchMethodException {
        when(mockRequest.getPath()).thenReturn(new PathParser("/test/does/not/exist"));
        RouteRef mockNotFound = Mockito.mock(RouteRef.class);
        when(mockNotFound.getInstance()).thenReturn(new DummyRoutable());
        Method dummyMethod = DummyRoutable.class.getMethod("dummy", Request.class, Response.class, MatchContext.class);
        when(mockNotFound.getInstanceMethod()).thenReturn(dummyMethod);
        subjectSimpleRouter.setNotFoundRoute(new BaseHTTPModel(mockNotFound));

        simpleContainer.handle(mockRequest, mockResponse);
        verify(mockNotFound, times(1)).handle(any(Request.class), any(Response.class), any(MatchContext.class));
    }

    private void testHandleRoute(PathHTTPContainer subject, HTTPNode container, String requestPath, boolean shouleBeHandled) {
        when(mockRequest.getPath()).thenReturn(new PathParser(requestPath));
        // Make the call as if it was used as a Container
        container.handle(mockRequest, mockResponse);
        verify(subject, times(shouleBeHandled ? 1 : 0)).handle(any(Request.class), any(Response.class), any(MatchContext.class));
    }

    private class DummyRoutable {
        public void dummy(Request request, Response response, MatchContext context) {

        }
    }
}