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
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by david on 12/02/2016.
 */
public class RouterTest {

    private Request mockRequest;
    private Response mockResponse;

    private Router subjectSimpleRouter;
    private Router subjectParamsRouter;
    private Router subjectIntParamsRouter;
    private Router subjectMultiParamsRouter;
    private HTTPNode simpleContainer;
    private HTTPNode paramsContainer;
    private HTTPNode intParamsContainer;
    private HTTPNode multiParamsContainer;

    @Before
    public void before() throws IOException {
        subjectSimpleRouter = spy(new Router("/test"));
        subjectParamsRouter = spy(new Router("/dummy/list/{id}"));
        subjectIntParamsRouter = spy(new Router("/dummy/list/{id:integer}"));
        subjectMultiParamsRouter = spy(new Router("/category/{category}/details/{id}"));

        // Prepare simple router
        simpleContainer = new DummyNode();
        simpleContainer.add(subjectSimpleRouter);

        // Prepare params router
        paramsContainer = new DummyNode();
        paramsContainer.add(subjectParamsRouter);

        // Prepare int params router
        intParamsContainer = new DummyNode();
        intParamsContainer.add(subjectIntParamsRouter);

        // Prepare multi-params router
        multiParamsContainer = new DummyNode();
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

        ArgumentCaptor<HTTPSession> argument = ArgumentCaptor.forClass(HTTPSession.class);
        verify(subjectParamsRouter).handle(argument.capture());

        Map<String, String> extractedParams = argument.getValue().context.getRouteParams();
        assertTrue(extractedParams.containsKey("id"));
        assertEquals("123", extractedParams.get("id"));
    }

    @Test
    public void shouldExtractManyParameters() {
        when(mockRequest.getPath()).thenReturn(new PathParser("/category/tips/details/123"));
        multiParamsContainer.handle(mockRequest, mockResponse);

        ArgumentCaptor<HTTPSession> argument = ArgumentCaptor.forClass(HTTPSession.class);
        verify(subjectMultiParamsRouter).handle(argument.capture());

        Map<String, String> extractedParams = argument.getValue().context.getRouteParams();
        assertTrue(extractedParams.containsKey("category"));
        assertEquals("tips", extractedParams.get("category"));
        assertTrue(extractedParams.containsKey("id"));
        assertEquals("123", extractedParams.get("id"));
    }


    private void testHandleRoute(Router subject, HTTPNode container, String requestPath, boolean shouleBeHandled) {
        when(mockRequest.getPath()).thenReturn(new PathParser(requestPath));
        // Make the call as if it was used as a Container
        container.handle(mockRequest, mockResponse);
        verify(subject, times(shouleBeHandled ? 1 : 0)).handle(any(HTTPSession.class));
    }

    private class DummyNode extends HTTPNode {
        public HTTPSession match(HTTPSession session) {
            session.context = new MatchContext(session.request.getPath().getPath(), true);
            return session;
        }
    }
}
