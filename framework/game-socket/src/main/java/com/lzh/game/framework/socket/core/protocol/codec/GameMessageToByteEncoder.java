package com.lzh.game.framework.socket.core.protocol.codec;

import com.lzh.game.framework.socket.utils.ByteBuffUtils;
import com.lzh.game.framework.socket.utils.Constant;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.protocol.message.MessageDefine;
import com.lzh.game.framework.socket.core.protocol.message.MessageManager;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class GameMessageToByteEncoder extends MessageToByteEncoder<Object> {

    private final MessageManager manager;

    public GameMessageToByteEncoder(MessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        /**
         * len: Varint32
         * msgId: sort
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data
         */
        try {
            if (msg instanceof AbstractCommand command) {
                var define = manager.findDefine(command.getMsgId());
                if (Objects.isNull(define)) {
                    log.error("Encoder. Not defined msg [{}]", command.getMsgId());
                    return;
                }
                out.markWriterIndex();
                var wrapper = this.allocateBuffer(ctx, msg, isPreferDirect());
                try {
                    encode(define, command.getData(), wrapper);
                    int bodyLen = wrapper.readableBytes();
                    int len = bodyLen + Constant.HEAD_MIN_LEN;
                    out.ensureWritable(len);

                    ByteBuffUtils.writeRawVarint32(out, len);
                    var msgId = command.getMsgId();
                    out.writeShort(msgId);
                    out.writeByte(command.getType());
                    out.writeInt(command.getRequestId());
                    out.writeBytes(wrapper, wrapper.readerIndex(), bodyLen);
                } finally {
                    wrapper.release();
                }
            } else if (msg instanceof ByteBuf) {
                out.writeBytes((ByteBuf) msg);
            } else if (msg instanceof byte[]) {
                out.writeBytes((byte[]) msg);
            }
            out.markWriterIndex();
        } catch (Exception e) {
            out.resetWriterIndex();
        }
    }

    protected void encode(MessageDefine define, Object msg, ByteBuf out) throws Exception {
        var msgId = define.getMsgId();
        int serializeType = manager.getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolMessage(serializeType);
        if (Objects.isNull(handler)) {
            log.error("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return;
        }
        handler.encode(define, msg, out);
    }
}
