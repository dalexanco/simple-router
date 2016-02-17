package com.thorbox.simplerouter.core.model.helper;

import org.mockito.Mockito;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.parse.PathParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by david on 12/02/2016.
 */
public class HttpTestHelper {

    public static Request mockRequest() {
        Request mockRequest = Mockito.mock(Request.class);
        when(mockRequest.getPath()).thenReturn(new PathParser(""));
        return mockRequest;
    }

    public static Response mockResponse() throws IOException {
        OutputStream mockNullOutputStream = new ByteArrayOutputStream(1024);
        Response mockResponse = Mockito.mock(Response.class);
        when(mockResponse.getPrintStream()).thenReturn(new PrintStream(mockNullOutputStream));
        return mockResponse;
    }
}
