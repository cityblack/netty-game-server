package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * @author zehong.l
 * @since 2024-11-12 11:08
 **/
public class LocalDateTimeSerializer implements Serializer<LocalDateTime> {

    @Override
    public LocalDateTime readObject(ByteBuf in, Class<LocalDateTime> clz) {
        long time = ByteBufUtils.readRawVarint64(in);
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(time), ZoneId.systemDefault());
    }

    @Override
    public void writeObject(ByteBuf out, LocalDateTime dateTime) {
        long time = dateTime.atZone(ZoneId.systemDefault()).toEpochSecond();
        ByteBufUtils.writeRawVarint64(out, time);
    }
}
