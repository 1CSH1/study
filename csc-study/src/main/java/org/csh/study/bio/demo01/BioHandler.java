package org.csh.study.bio.demo01;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Date;

/**
 * 服务端处理
 */
public class BioHandler implements Runnable {

    private Socket socket;

    public BioHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

        BufferedReader in = null;
        PrintWriter out = null;
        try {
            in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            out = new PrintWriter(this.socket.getOutputStream(), true);

            while (true) {
                String line = in.readLine();
                if (null == line) {
                    break;
                }
                System.out.println("server recieve " + line);
                if ("current time".equalsIgnoreCase(line)) {
                    out.println(new Date(System.currentTimeMillis()));
                } else {
                    out.println("your content is " + line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
