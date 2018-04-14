package org.csh.study.nio.chat.myself.client;

import org.csh.study.nio.chat.myself.ChatConstant;
import org.csh.study.nio.chat.myself.controller.ClientController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * 聊天客户端
 */
public class ChatRoomClient {

    private Selector selector = null;
    private SocketChannel sc = null;
    private String name = "";

    private ClientController clientController = new ClientController();

    public ChatRoomClient() {
        try {
            this.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() throws IOException {
        selector = Selector.open();
        sc = SocketChannel.open(new InetSocketAddress(ChatConstant.host, ChatConstant.port));
        sc.configureBlocking(false);
        sc.register(selector, SelectionKey.OP_READ);

        new ClientThread().start();

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if ("".equals(line)) {
                continue;
            }
            if ("".equals(name)) {
                name = line;
                line = name + ChatConstant.SPILTSTRING;
            } else {
                line = name + ChatConstant.SPILTSTRING + line;
            }
            sc.write(ChatConstant.charset.encode(line));
        }
    }

    /**
     * 客户端线程
     */
    class ClientThread extends Thread {
        @Override
        public void run() {
            try {
                while (true) {
                    int readChannels = selector.select();
                    if (0 == readChannels) {
                        continue;
                    }
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        dealWithSelectionKey(key);
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void dealWithSelectionKey(SelectionKey key) throws IOException {
            if (key.isReadable()) {
                SocketChannel sc = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                StringBuilder content = new StringBuilder();
                while (sc.read(buffer) > 0) {
                    buffer.flip();
                    content.append(ChatConstant.charset.decode(buffer));
                }

                System.out.println(content);
                key.interestOps(SelectionKey.OP_READ);
            }
        }
    }

    public static void main(String[] args) {
        new ChatRoomClient();
    }

}

