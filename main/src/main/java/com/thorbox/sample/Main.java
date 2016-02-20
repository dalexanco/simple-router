package com.thorbox.sample;

import com.thorbox.simplerouter.annotation.AnnotationRouter;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * Created by david on 04/02/2016.
 */
public class Main {

    public static void main(String[] args) {

        try {
            AnnotationRouter router = new AnnotationRouter();
            router.add(new SampleRouter());

            Server server = new ContainerServer(router);
            Connection connection = new SocketConnection(server);
            SocketAddress address = new InetSocketAddress(2222);
            connection.connect(address);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
