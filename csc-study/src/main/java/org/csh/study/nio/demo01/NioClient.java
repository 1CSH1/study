package org.csh.study.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NioClient {

    public static void main(String[] args) {
        int port = 1010;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        new Thread(new NioClientHandler(port)).start();
    }
}
