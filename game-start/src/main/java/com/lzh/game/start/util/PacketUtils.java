package com.lzh.game.start.util;

import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.session.Session;
import com.lzh.game.framework.socket.exception.EncodeSerializeException;
import com.lzh.game.framework.socket.starter.server.SpringServer;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.SessionPlayerManage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;

import java.util.Collection;

public class PacketUtils {

    private final static ThreadLocal<ByteBuf> LOCAL_BUFF = new ThreadLocal<>() {
        @Override
        protected ByteBuf initialValue() {
            return PooledByteBufAllocator.DEFAULT.buffer();
        }
    };

    public static void send(Player player, Object message) {
        SessionPlayerManage sessionManage = ApplicationUtils.getBean(SessionPlayerManage.class);
        Session session = sessionManage.getSessionByPlayer(player.getKey());
        send(session, message);
    }

    public static void send(Session session, Object message) {
        if (message instanceof ByteBuf buf) {
            session.write(buf);
        } else {
            session.oneWay(message);
        }
    }

    public static void broadcast(Collection<Player> players, Object message) {
        var serialize = MessageSerializeManager.getInstance()
                .getProtocolSerialize(Constant.DEFAULT_SERIAL_SIGN);
        try {
            var out = LOCAL_BUFF.get();
            serialize.encode(ApplicationUtils.getBean(MessageDefine.class), message, out);
            for (Player player : players) {
                send(player, out);
            }
        } catch (EncodeSerializeException e) {
            throw new RuntimeException(e);
        }
    }

    private PacketUtils() {
    }
}
