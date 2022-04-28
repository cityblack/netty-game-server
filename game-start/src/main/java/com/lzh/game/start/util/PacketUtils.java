package com.lzh.game.start.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageOrBuilder;
import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.common.serialization.ProtoBufUtils;
import com.lzh.game.socket.core.coder.ExchangeProtocol;
import com.lzh.game.framework.cmd.CmdMappingManage;
import com.lzh.game.socket.core.session.Session;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.player.service.SessionPlayerManage;

import java.util.Objects;

public class PacketUtils {

    public static void send(Player player, int cmd) {
        send(player, cmd, null);
    }

    public static void send(Player player, int cmd, Object pack) {

        SessionPlayerManage sessionManage = ApplicationUtils.getBean(SessionPlayerManage.class);
        Session session = sessionManage.getSessionByPlayer(player.getKey());
        send(session, cmd, pack);
    }

    public static void send(Session session, int cmd, Object pack) {
        checkCmd(cmd);
        session.write(toProBufResponse(cmd, pack));
    }

   private static void checkCmd(int cmd) {
        if (!ApplicationUtils.getBean(CmdMappingManage.class).contain(cmd)) {
            throw new IllegalArgumentException("Not register [" + cmd + "] proto");
        }
   }

    private PacketUtils() {}

    public static MessageOrBuilder toProBufResponse(int cmd, Object data) {
        byte[] bytes = Objects.isNull(data) ? new byte[]{} : ProtoBufUtils.serialize(data);

        ExchangeProtocol.Response response = ExchangeProtocol
                .Response
                .newBuilder()
                .setHead(ExchangeProtocol.Response.Head.newBuilder()
                        .setCmd(cmd)
                        .setLen(bytes.length)
                        .build())
                .setData(ByteString.copyFrom(bytes))
                .build();
        return response;
    }
}
