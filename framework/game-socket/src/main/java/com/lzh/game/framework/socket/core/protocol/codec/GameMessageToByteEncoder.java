package com.lzh.game.framework.socket.core.protocol.codec;

import com.lzh.game.framework.socket.core.bootstrap.BootstrapContext;
import com.lzh.game.framework.socket.utils.ByteBuffUtils;
import com.lzh.game.framework.socket.core.protocol.AbstractCommand;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerialize;
import com.lzh.game.framework.socket.core.protocol.serial.MessageSerializeManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class GameMessageToByteEncoder extends MessageToByteEncoder<Object> {

    private final BootstrapContext<?> context;

    public GameMessageToByteEncoder(BootstrapContext<?> context) {
        this.context = context;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        /**
         * len: Varint32
         * msgId: sort
         * type: request / response / one way byte
         * request: int (varint32 value >= 2097152 need 4 byte or more)
         * data: Object Serializable data
         */
        try {
            if (msg instanceof AbstractCommand command) {

                out.markWriterIndex();
                var wrapper = this.allocateBuffer(ctx, msg, isPreferDirect());
                try {
                    var msgId = command.getMsgId();
                    wrapper.writeShort(msgId);
                    wrapper.writeByte(command.getType());
                    wrapper.writeInt(command.getRequestId());
                    encode(command, wrapper);

                    int len = wrapper.readableBytes();
                    out.ensureWritable(len + 4);
                    ByteBuffUtils.writeRawVarint32(out, len);
                    out.writeBytes(wrapper);
                } finally {
                    wrapper.release();
                }
            } else if (msg instanceof ByteBuf buf) {
                out.writeBytes(buf);
            } else if (msg instanceof byte[] bytes) {
                out.writeBytes(bytes);
            }
            out.markWriterIndex();
        } catch (Exception e) {
            out.resetWriterIndex();
            throw e;
        }
    }

    protected void encode(AbstractCommand command, ByteBuf out) throws Exception {
        if (command.getData() instanceof byte[] bytes) {
            out.writeBytes(bytes);
            return;
        } else if (command.getData() instanceof ByteBuf buf) {
            out.writeBytes(buf);
            return;
        }
        var define = context.getMessageManager().findDefine(command.getMsgId());
        if (Objects.isNull(define)) {
            log.error("Encoder. Not defined msg [{}]", command.getMsgId());
            return;
        }
        var msgId = define.getMsgId();
        int serializeType = context.getMessageManager().getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolSerialize(serializeType);
        if (Objects.isNull(handler)) {
            log.error("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return;
        }
        handler.encode(define, command.getData(), out);
    }
}
