package org.csh.study.nio.demo01;

public class NioServer {

    public static void main(String[] args) {
        int port = 1010;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        NioServerHandler nioServerHandler = new NioServerHandler(port);
        new Thread(nioServerHandler, "NioServer").start();
    }

}
