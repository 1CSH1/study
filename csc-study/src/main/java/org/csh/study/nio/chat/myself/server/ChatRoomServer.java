package org.csh.study.nio.chat.myself.server;

import org.csh.study.nio.chat.myself.ChatConstant;
import org.csh.study.nio.chat.myself.controller.ServerController;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * 聊天服务器
 */
public class ChatRoomServer {

    private Selector selector = null;
    private ServerSocketChannel serverSocketChannel = null;

    private ServerController serverController = new ServerController();

    public ChatRoomServer() {
        try {
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化
     */
    private void init() throws IOException {
        // 初始化 selector
        this.selector = Selector.open();
        // 初始化 serverSocketChannel
        this.serverSocketChannel = ServerSocketChannel.open();
        // 绑定域名端口
        serverSocketChannel.bind(new InetSocketAddress(ChatConstant.host, ChatConstant.port));
        // 设置非阻塞
        serverSocketChannel.configureBlocking(false);
        // 注册到 selector 上，设置为监听
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        // 监听
        this.monitor();
    }

    /**
     * 监听
     */
    private void monitor() throws IOException {
        while (true) {
            int readChannels = this.selector.select();
            // 没有通道则循环
            if (0 == readChannels) {
                continue;
            }

            // 处理通道信息
            Set<SelectionKey> selectionKeys = this.selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                dealWithSelectionKey(key);
            }
        }
    }

    /**
     * 处理不同的 SelectionKey
     */
    private void dealWithSelectionKey(SelectionKey key) throws IOException {
        // 接收新的连接
        if (key.isAcceptable()) {
            // 接收通道
            SocketChannel socketChannel = this.serverSocketChannel.accept();
            // 设置为非阻塞
            socketChannel.configureBlocking(false);
            // 将该通道绑定为
            socketChannel.register(this.selector, SelectionKey.OP_READ);
            // 接收 OP_ACCEPT 处理
            key.interestOps(SelectionKey.OP_ACCEPT);

            System.out.println("Server is listening from client :" + socketChannel.getRemoteAddress());
            socketChannel.write(ChatConstant.charset.encode("Please input your name."));
        }

        // 接收客户端发来的信息
        if (key.isReadable()) {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            StringBuilder content = new StringBuilder();
            // 读取客户端发来的内容
            while (sc.read(buffer) > 0) {
                buffer.flip();
                content.append(ChatConstant.charset.decode(buffer));
            }

            // 有数据
            if (content.length() > 0) {
                String[] contents = content.toString().split(ChatConstant.SPILTSTRING);
                if (null != contents && contents.length == 1) {
                    // 注册
                    String name = contents[0];
                    serverController.addUser(name, 10, 1);
                    int num = onlineNumber();
                    String message = "welcome " + name + " to chat room! Online numbers:" + num;
                    // 广播给所有用户
                    broadCast(null, message);
                } else if (null != contents && contents.length > 1) {
                    // 注册完，发送信息
                    String name = contents[0];
                    StringBuilder message = new StringBuilder();
                    message.append(name)
                            .append(" say: ")
                            .append(content.substring(name.length() + ChatConstant.SPILTSTRING.length()));
                    if (serverController.isExists(name)) {
                        // 不发送给自己
                        broadCast(sc, message.toString());
                    }
                }
            }
        }

    }

    /**
     *  广播给除了except的用户
     */
    private void broadCast(SocketChannel except, String message) throws IOException {
        for (SelectionKey key : selector.keys()) {
            Channel targetChannel = key.channel();
            if (targetChannel instanceof SocketChannel && targetChannel != except) {
                SocketChannel sc = (SocketChannel) targetChannel;
                sc.write(ChatConstant.charset.encode(message));
            }
        }
    }

    /**
     * 计算在线人数
     */
    private int onlineNumber() {
        int count = 0;
        for (SelectionKey key : selector.keys()) {
            if (key.channel() instanceof SocketChannel) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        new ChatRoomServer();
    }

}
