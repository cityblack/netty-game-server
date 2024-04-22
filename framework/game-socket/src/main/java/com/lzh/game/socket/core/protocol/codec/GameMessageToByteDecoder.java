package com.lzh.game.socket.core.protocol.codec;

import com.lzh.game.socket.Constant;
import com.lzh.game.socket.core.message.MessageManager;
import com.lzh.game.socket.core.protocol.AbstractCommand;
import com.lzh.game.socket.core.protocol.MessageSerialize;
import com.lzh.game.socket.core.protocol.MessageSerializeManager;
import com.lzh.game.socket.utils.ByteBuffUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public class GameMessageToByteDecoder extends MessageToByteEncoder<Object> {

    private MessageManager manager;

    public GameMessageToByteDecoder(MessageManager manager) {
        this.manager = manager;
    }

    @Override
    public void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        /**
         * len: int
         * msgId: msg short
         * type: request / response / one way byte
         * request: int
         * data: Object Serializable data. bytes
         */
        try {
            if (msg instanceof AbstractCommand command) {
                if (!manager.hasMessage(command.getMsgId())) {
                    log.error("Encoder. Not defined msg [{}]", command.getMsgId());
                    return;
                }
                var wrapper = this.allocateBuffer(ctx, msg, isPreferDirect());
                try {
                    int msgId = command.getMsgId();
                    wrapper.writeShort(msgId);
                    wrapper.writeByte(command.getType());
                    wrapper.writeInt(command.getRequestId());
                    encode(msgId, command.getData(), wrapper);

                    int bodyLen = wrapper.readableBytes();
                    out.ensureWritable(bodyLen + 4);
                    out.writeInt(bodyLen);
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

    protected void encode(int msgId, Object msg, ByteBuf out) throws Exception {
        int serializeType = manager.getSerializeType(msgId);
        MessageSerialize handler = MessageSerializeManager.getInstance()
                .getProtocolMessage(serializeType);
        if (Objects.isNull(handler)) {
            log.error("Not defined msg serialize type [{}-{}]", msgId, serializeType);
            return;
        }
        handler.encode(msgId, msg, out);
    }

    static int computeRawVarint32Size(int value) {
        if ((value & -128) == 0) {
            return 1;
        } else if ((value & -16384) == 0) {
            return 2;
        } else if ((value & -2097152) == 0) {
            return 3;
        } else {
            return (value & -268435456) == 0 ? 4 : 5;
        }
    }
}
