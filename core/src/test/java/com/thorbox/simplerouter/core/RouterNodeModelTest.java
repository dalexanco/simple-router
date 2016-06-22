package com.thorbox.simplerouter.core;

import com.thorbox.simplerouter.core.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.MatchContext;
import org.junit.Before;
import org.junit.Test;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;

import java.io.IOException;

import static org.mockito.Mockito.*;

/**
 * Created by david on 11/02/2016.
 */
public class RouterNodeModelTest {

    private Request mockRequest;
    private Response mockResponse;

    private DummyRouter subjectRouter;

    private class DummyRouter extends HTTPNode {
        private final String name;
        private boolean matchResult;

        public DummyRouter(String name) {
            this.name = name;
            this.matchResult = true;
        }

        public DummyRouter(String name, boolean matchResult) {
            this(name);
            this.matchResult = matchResult;
        }

        @Override
        public MatchContext match(Request request, Response response, MatchContext lastMatch) {
            return new MatchContext(matchResult);
        }

        @Override
        public void handle(Request request, Response response, MatchContext currentPath) {
            System.out.println("Handle : " + this.name);
            super.handle(request, response, currentPath);
        }

        public void setMatchResult(boolean matchResult) {
            this.matchResult = matchResult;
        }
    }

    @Before
    public void before() throws IOException {
        System.out.println("--- Initialize graph...");
        subjectRouter = spy(new DummyRouter("The one"));
        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();
    }

    @Test
    public void testSpecificRouteCatching() {
        // Build a graph
        HTTPNode graph = new DummyRouter("Graph");
        HTTPNode level1 = new DummyRouter("Level 1");
        HTTPNode level2 = new DummyRouter("Level 2");
        level2.add(new DummyRouter("Level 2 - Node 1", false));
        level2.add(subjectRouter);
        level1.add(new DummyRouter("Level 1 - Node 1", false));
        level1.add(new DummyRouter("Level 1 - Node 2", false));
        level1.add(level2);
        graph.add(new DummyRouter("Graph - Node 1", false));
        graph.add(level1);
        // Make the call
        graph.handle(mockRequest, mockResponse);
        // Then check
        verify(subjectRouter, atLeastOnce()).handle(any(Request.class), any(Response.class), any(MatchContext.class));
    }

    @Test
    public void testNaturalGraphHandling() {
        // Build a graph
        HTTPNode graph = new DummyRouter("Graph");
        HTTPNode level1 = new DummyRouter("Level 1");
        HTTPNode level2 = new DummyRouter("Level 2");
        level2.add(subjectRouter);
        level1.add(level2);
        level1.add(new DummyRouter("Level 1 - Node 1"));
        level1.add(new DummyRouter("Level 1 - Node 2"));
        graph.add(level1);
        graph.add(new DummyRouter("Graph - Node 1"));
        // Make the call
        graph.handle(mockRequest, mockResponse);
        // Then check
        verify(subjectRouter, atLeastOnce()).handle(any(Request.class), any(Response.class), any(MatchContext.class));
    }

}
