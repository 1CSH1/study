package org.csh.study.aio.demo01;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 接收请求逻辑处理
 */
public class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, AioServerHandler>{

    @Override
    public void completed(AsynchronousSocketChannel result, AioServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment, this);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AioServerHandler attachment) {
        exc.printStackTrace();
        attachment.countDownLatch.countDown();
    }
}
