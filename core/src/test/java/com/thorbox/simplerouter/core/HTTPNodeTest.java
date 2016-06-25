package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.HTTPSession;
import com.thorbox.simplerouter.core.model.MatchContext;
import com.thorbox.simplerouter.core.route.RouteRef;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import java.io.IOException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

/**
 * Created by david on 11/02/2016.
 */
public class HTTPNodeTest {

    private Request mockRequest;
    private Response mockResponse;

    private HTTPNode subjectHTTPNode;

    @Before
    public void before() throws IOException, NoSuchMethodException {
        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
        subjectHTTPNode = spy(new DummyNode());
    }

    @Test
    public void handleShouldCallRoutable() throws NoSuchMethodException {
        HTTPNode router = new DummyNode();
        router.add(subjectHTTPNode);
        router.handle(new HTTPSession(mockRequest, mockResponse, new MatchContext(true)));
        verify(subjectHTTPNode, times(1)).handle(any(HTTPSession.class));
    }

    @Test
    public void shouldCallNoRouteFound() throws NoSuchMethodException {
        when(mockRequest.getPath()).thenReturn(new PathParser("/test/does/not/exist"));
        HTTPNode mockNotFound = Mockito.mock(HTTPNode.class);
        subjectHTTPNode.setNotFoundNode(mockNotFound);
        subjectHTTPNode.handle(new HTTPSession(mockRequest, mockResponse, new MatchContext(true)));
        verify(mockNotFound, times(1)).handle(any(HTTPSession.class));
    }

    @Test
    public void shouldCallMiddlewares() {
        HTTPNode middlewareMock = Mockito.mock(HTTPNode.class);
        when(middlewareMock.match(any(HTTPSession.class)))
                .thenReturn(new HTTPSession(mockRequest, mockResponse, new MatchContext(true)));
        subjectHTTPNode.middleware(middlewareMock);
        subjectHTTPNode.handle(new HTTPSession(mockRequest, mockResponse, new MatchContext(true)));
        verify(middlewareMock, times(1)).handle(any(HTTPSession.class));
    }

    private class DummyNode extends HTTPNode {
        @Override
        public HTTPSession match(HTTPSession session) {
            session.context = new MatchContext(true);
            return session;
        }
    }
}
