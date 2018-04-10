package org.csh.study.nio.chat.myself.server;

import org.csh.study.nio.chat.myself.ChatConstant;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * 聊天服务器
 */
public class ChatRoomServer {

    Selector selector = null;
    ServerSocketChannel serverSocketChannel = null;

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
    private void dealWithSelectionKey(SelectionKey key) {
        if (key.isReadable()) {

        }

        if (key.isAcceptable()) {

        }

        if (key.isWritable()) {

        }

        if (key.isConnectable()) {

        }
    }

}
