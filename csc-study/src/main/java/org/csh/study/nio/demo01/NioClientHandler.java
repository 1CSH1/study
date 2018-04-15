package org.csh.study.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 客户端逻辑处理类
 */
public class NioClientHandler implements Runnable {

    private Selector selector = null;
    private SocketChannel socketChannel = null;
    private volatile boolean isStop = false;
    private int port;

    public NioClientHandler(int port) {
        try {
            this.port = port;
            this.selector = Selector.open();
            this.socketChannel = SocketChannel.open();
            this.socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            doConnection();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            while (!isStop) {
                int num = this.selector.select(1000);
                if (num <= 0) {
                    continue;
                }
                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    try {
                        doHandle(key);
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (null != key) {
                            key.cancel();
                            if (null != key.channel()) {
                                key.channel().close();
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接
     */
    private void doConnection() throws IOException {
        if (this.socketChannel.connect(new InetSocketAddress(port))) {
            this.socketChannel.register(this.selector, SelectionKey.OP_READ);
            doWrite(this.socketChannel);
        } else {
            this.socketChannel.register(this.selector, SelectionKey.OP_CONNECT);
        }
    }

    /**
     * 写数据
     */
    private void doWrite(SocketChannel sc) throws IOException {
        byte[] bytes = "current time".getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        sc.write(buffer);
        if (!buffer.hasRemaining()) {
            System.out.println("hasRemaining");
        }
    }

    /**
     * 客户端处理逻辑
     */
    private void doHandle(SelectionKey key) throws IOException {
        if (key.isValid()) {
            SocketChannel sc = (SocketChannel) key.channel();
            if (key.isConnectable()) {
                if (sc.finishConnect()) {
                    sc.register(this.selector, SelectionKey.OP_READ);
                    doWrite(sc);
                } else {
//                    System.exit(1);
                }
            }

            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(buffer);
                if (readBytes > 0) {
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    System.out.println("the response body is " + result);
                    this.isStop = true;
                } else if (readBytes < 0) {
                    key.cancel();
                    sc.close();
                } else {

                }
            }
        }
    }
}
