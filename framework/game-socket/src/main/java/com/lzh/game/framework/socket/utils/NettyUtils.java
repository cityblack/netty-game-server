package com.lzh.game.framework.socket.utils;

import com.lzh.game.framework.socket.core.bootstrap.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.util.Map;

/**
 * @author zehong.l
 * @since 2024-08-01 15:24
 **/
public class NettyUtils {

    public static void serverBootAddOptions(ServerBootstrap bootstrap, NettyProperties netty) {
        for (Map.Entry<String, Object> entry : netty.getChannelOptions().entrySet()) {
            bootstrap.option(ChannelOption.valueOf(entry.getKey()), entry.getValue());
        }
        for (Map.Entry<String, Object> entry : netty.getChildOptions().entrySet()) {
            bootstrap.childOption(ChannelOption.valueOf(entry.getKey()), entry.getValue());
        }
    }

    public static EventLoopGroup group(boolean useEpoll, int core) {
        if (useEpoll && Epoll.isAvailable()) {
            return new EpollEventLoopGroup(core);
        } else if (useEpoll && KQueue.isAvailable()) {
            return new KQueueEventLoopGroup();
        }
        return new NioEventLoopGroup(core);
    }

    public static Class<? extends ServerSocketChannel> serverChannelType(boolean useEpoll) {
        if (useEpoll && Epoll.isAvailable()) {
            return EpollServerSocketChannel.class;
        } else if (useEpoll && KQueue.isAvailable()) {
            return KQueueServerSocketChannel.class;
        }
        return NioServerSocketChannel.class;
    }
}
