package org.csh.study.bio.demo02;

import org.csh.study.bio.demo01.BioHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * BIO伪异步
 */
public class BioServer {

    public static void main(String[] args) {
        int port = 1100;
        ServerSocket serverSocket = null;
        try {
             serverSocket = new ServerSocket(port);
             BioServerHandlerExecutePool executePool =
                     new BioServerHandlerExecutePool(50, 10000);
             while (true) {
                 Socket socket = serverSocket.accept();
                 executePool.execute(new BioHandler(socket));
             }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != serverSocket) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
