package org.csh.study.bio.demo01;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO服务端
 */
public class BioServer {

    public static void main(String[] args) {
        int port = 1100;
        if (null != args && args.length == 1) {
            port = Integer.parseInt(args[0]);
        }

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(new BioHandler(socket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != serverSocket) {
                System.out.println("Server closing");
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverSocket = null;
            }
        }

    }

}
