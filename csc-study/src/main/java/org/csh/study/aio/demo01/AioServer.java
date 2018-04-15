package org.csh.study.aio.demo01;

public class AioServer {

    public static void main(String[] args) {
        int port = 1111;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        AioServerHandler aioServerHandler = new AioServerHandler(port);
        new Thread(aioServerHandler, "aio-server").start();
    }

}
