package com.lzh.game.framework.socket.core.protocol.codec;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.protocol.Request;
import com.lzh.game.framework.socket.core.protocol.Response;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import com.lzh.game.framework.socket.core.session.SessionUtils;
import com.lzh.game.framework.socket.utils.ByteBuffUtils;
import com.lzh.game.framework.socket.Constant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

@Slf4j
public class ByteToGameMessageDecoder extends ByteToMessageDecoder {

    private final BootstrapContext<?> context;

    public ByteToGameMessageDecoder(BootstrapContext<?> context) {
        this.context = context;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        /**
         * len: Varint32
         * msgId: sort
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data
         */
        log.debug("[{}] message parse", ctx.channel().id().asLongText());
        in.markReaderIndex();
        int preIndex = in.readerIndex();
        int len = ByteBuffUtils.readRawVarint32(in);
        if (preIndex != in.readerIndex() && len < 0) {
            throw new CorruptedFrameException("negative length: " + len);
        }
        if (in.readableBytes() < len) {
            in.resetReaderIndex();
            return;
        }
        int startIndex = in.readerIndex();
        short msgId = in.readShort();
        byte type = in.readByte();
        int requestId = in.readInt();
        int dataLen = len - in.readerIndex() + startIndex;
        log.debug("parse to [{}] - [{}]", ctx.channel().id().asLongText(), msgId);
        Object o = decode(ctx, in.readSlice(dataLen), msgId, dataLen);
        if (Objects.isNull(o)) {
            return;
        }
        AbstractCommand command = Constant.isRequest(type) ?
                Request.of(msgId, context.getMessageManager().findDefine(msgId), requestId, o) : Response.of(msgId, requestId, o);

        if (command instanceof Request request) {
            request.setSession(SessionUtils.channelGetSession(ctx.channel()));
        }
        command.setType(type);
        out.add(command);
    }

    public Object decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, short msgId, int dataLen) throws Exception {
        if (context.getProperties().isBodyDateToBytes() && Constant.canParseToBytes(msgId)) {
            byte[] bytes = new byte[dataLen];
            in.readBytes(bytes, in.readerIndex(), bytes.length);
            in.markReaderIndex();
            return bytes;
        }
        var define = context.getMessageManager().findDefine(msgId);
        if (!context.getMessageManager().hasDefined(msgId)) {
            log.warn("Not defined msgId [{}]", msgId);
            return null;
        }
        int serializeType = context.getMessageManager().getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolSerialize(serializeType);
        if (Objects.isNull(handler)) {
            log.warn("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return null;
        }
        return handler.decode(define, in);
    }

}
