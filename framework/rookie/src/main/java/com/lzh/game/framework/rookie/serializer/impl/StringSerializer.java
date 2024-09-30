package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author zehong.l
 * @since 2024-09-13 16:20
 **/
public class StringSerializer implements Serializer<String> {

    @Override
    public String readObject(ByteBuf in, Class<String> clz) {
        var len = ByteBufUtils.readRawVarint32(in);
        if (len == 0) {
            return null;
        }
        return new String(ByteBufUtils.readBytes(in, len), StandardCharsets.UTF_8);
    }

    @Override
    public void writeObject(ByteBuf out, String value) {
        if (Objects.isNull(value)) {
            ByteBufUtils.writeRawVarint32(out, 0);
        } else {
            byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
            ByteBufUtils.writeRawVarint32(out, bytes.length);
            ByteBufUtils.writeBytes(out, bytes);
        }
    }
}
