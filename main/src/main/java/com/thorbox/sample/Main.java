package com.thorbox.sample;

import com.thorbox.sample.model.SampleRouter;
import com.thorbox.simplerouter.annotation.AnnotationRouter;
import org.simpleframework.http.core.ContainerServer;
import org.simpleframework.transport.Server;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by david on 04/02/2016.
 */
public class Main {

    public static void main(String[] args) {
        /*
        try {
            AnnotationRouter router = new AnnotationRouter();
            router.add(new SampleRouter());

            Server server = new ContainerServer(router);
            Connection connection = new SocketConnection(server);
            SocketAddress address = new InetSocketAddress(2222);
            connection.connect(address);

            */

            Pattern p = Pattern.compile("cat");
            Matcher m = p.matcher("one cat two cats in the yard");
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                Matcher dog = m.appendReplacement(sb, "dog");
                System.out.println("test");

            }
            m.appendTail(sb);
            System.out.println(sb.toString());
        /*
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}
