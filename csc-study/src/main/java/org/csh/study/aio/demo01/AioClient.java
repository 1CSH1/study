package org.csh.study.aio.demo01;

public class AioClient {

    public static void main(String[] args) {
        int port = 1111;
        if (null != args && args.length > 0) {
            port = Integer.parseInt(args[0]);
        }

        new Thread(new AioClientHandler(port)).start();
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
