package org.csh.study.nio.demo01;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO 服务端处理
 */
public class NioServerHandler implements Runnable{

    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;
    private volatile boolean isStop = false;

    public NioServerHandler(int port) {
        try {
            // 创建 selector
            this.selector = Selector.open();
            // 创建 channel
            this.serverSocketChannel = ServerSocketChannel.open();
            // 设置为非阻塞模式
            this.serverSocketChannel.configureBlocking(false);
            // 绑定端口
            this.serverSocketChannel.bind(new InetSocketAddress(port));
            // 将 channel 注册到 selector 上，设置为 OP_ACCEPT
            this.serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Server is start in port : " + port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void run() {
        try {
            while (!isStop) {
                // selector 每 1 秒被唤醒一次，查询有多少个 channel 已经准备好做 IO 操作了
                int num = this.selector.select(1000);
                if (num <= 0) {
                    // 没有 channel 准备好，就重新循环
                    continue;
                }

                Set<SelectionKey> keys = this.selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    try {
                        doHandler(key);
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
        } finally {
            if (null != selector) {
                try {
                    selector.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 处理逻辑
     */
    private void doHandler(SelectionKey key) throws IOException {
        if (key.isValid()) {
            // 接收新的连接
            if (key.isAcceptable()) {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(this.selector, SelectionKey.OP_READ);
            }

            // 读取内容
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int readBytes = sc.read(buffer);
                if (readBytes > 0) {
                    // 读取到数据
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String line = new String(bytes, "UTF-8");
                    System.out.println("Server recieve content is " + line);
                    if ("current time".equalsIgnoreCase(line)) {
                        doWrite(sc, new Date(System.currentTimeMillis()).toString());
                    } else {
                        doWrite(sc, "your content is " + line);
                    }
                } else if (readBytes < 0) {
                    //没有读到数据
                    key.cancel();
                    sc.close();
                } else {
                    // 读到 0 字节
                }
            }
        }

    }

    /**
     * 写数据
     */
    private void doWrite(SocketChannel sc, String line) throws IOException {
        if (null != line && line.trim().length() > 0) {
            // 创建 buffer，将数据写进 buffer
            byte[] bytes = line.getBytes();
            ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
            buffer.put(bytes);
            // 写转为读
            buffer.flip();
            // 将 buffer 数据写入到 sc
            sc.write(buffer);
        }
    }
}
