package com.thorbox.simplerouter.core.model;

import com.thorbox.simplerouter.core.model.helper.HttpTestHelper;
import com.thorbox.simplerouter.core.model.matcher.MatchContext;
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

    private BaseRouteModel subjectRouteModel;

    @Before
    public void before() throws IOException, NoSuchMethodException {
        mockRequest = HttpTestHelper.mockRequest();
        mockResponse = HttpTestHelper.mockResponse();

        Method dummyRoutableMethod = DummyRouter.class.getMethod("dummyRoutableMethod", Request.class, Response.class);
        Routable routable = Routable.from(new DummyRouter(), dummyRoutableMethod);
        subjectRouteModel = spy(new BaseRouteModel(routable));
    }

    @Test
    public void handleShouldCallRoutable() throws NoSuchMethodException {
        Routable mockRoutable = Mockito.mock(Routable.class);
        when(mockRoutable.getInstance()).thenReturn(new DummyRoutable());
        when(mockRoutable.getInstanceMethod()).thenReturn(DummyRoutable.class.getMethod("dummy", Request.class, Response.class));
        subjectRouteModel = new BaseRouteModel(mockRoutable);
        RouteNodeModel router = new DummyRouter();
        router.add(subjectRouteModel);
        router.handle(mockRequest, mockResponse);
        verify(mockRoutable, times(1)).handle(any(), any());
    }

    private class DummyRoutable {
        public void dummy(Request req, Response resp) {}
    }

    private class DummyRouter extends RouteNodeModel {

        public void dummyRoutableMethod(Request request, Response response) {
        }

        @Override
        public MatchContext match(Request request, Response response, MatchContext currentPath) {
            return new MatchContext(true);
        }
    }
}
