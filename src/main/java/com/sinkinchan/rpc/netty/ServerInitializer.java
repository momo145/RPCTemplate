package com.sinkinchan.rpc.netty;

import com.sinkinchan.rpc.netty.handler.ServerHandler;
import com.sinkinchan.rpc.netty.proto.RichManProto;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerInitializer {
    private Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 用于分配处理业务线程的线程组个数
     */
    protected static final int BIZGROUPSIZE = Runtime.getRuntime().availableProcessors() * 2;
    /**
     * 业务出现线程大小
     */
    protected static final int BIZTHREADSIZE = 4;
    private int timeout = 3600;
    private int port = 8181;

    public void bind() throws Exception {
        // 配置服务端的NIO线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup(BIZGROUPSIZE);
        EventLoopGroup workerGroup = new NioEventLoopGroup(BIZTHREADSIZE);
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.INFO)).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new ProtobufVarint32FrameDecoder());
                    ch.pipeline().addLast(new ProtobufDecoder(RichManProto.RichMan.getDefaultInstance()));
                    ch.pipeline().addLast(new ServerHandler());
                }
            });

            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(port).sync();

            this.logger.info("server started at port " + this.port + '.');

            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放线程池资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public int getTimeout() {
        return timeout;
    }

    public ServerInitializer setTimeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ServerInitializer setPort(int port) {
        this.port = port;
        return this;
    }
}