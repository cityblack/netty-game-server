package com.lzh.game.framework.socket.core.protocol.codec;

import com.lzh.game.framework.socket.Constant;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class GameByteToMessageDecoder extends ByteToMessageDecoder {

    private MessageManager manager;

    private boolean dataToBytes;

    public GameByteToMessageDecoder(MessageManager manager) {
        this.manager = manager;
    }

    public GameByteToMessageDecoder(MessageManager manager, boolean dataToBytes) {
        this.manager = manager;
        this.dataToBytes = dataToBytes;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * len: int
         * msgId: sort
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data
         */
        if (in.readableBytes() < Constant.HEAD_MIN_LEN) {
            return;
        }
        in.markReaderIndex();
        int len = in.readInt();
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        int startIndex = in.readerIndex();
        byte type = in.readByte();
        short msgId = in.readShort();
        int requestId = in.readerIndex();
        int dataLen = len - in.readableBytes() + startIndex;

        Object o = decode(ctx, in, msgId, dataLen);
        if (Objects.isNull(o)) {
            return;
        }
        AbstractCommand command = Constant.isRequest(type) ?
                Request.of(manager.findDefine(msgId), requestId, o) : Response.of(msgId, requestId, o);
        command.setType(type);
        out.add(command);
    }

    public Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, int msgId, int dataLen) throws Exception {
        if (dataToBytes) {
            byte[] bytes = new byte[dataLen];
            in.readBytes(bytes, in.readerIndex(), bytes.length);
            in.markReaderIndex();
            return bytes;
        }
        var define = manager.findDefine(msgId);
        if (!manager.hasMessage(msgId)) {
            log.warn("Not defined msgId [{}]", msgId);
            return null;
        }
        int serializeType = manager.getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolMessage(serializeType);
        if (Objects.isNull(handler)) {
            log.warn("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return null;
        }
        return handler.decode(define, in);
    }

}
