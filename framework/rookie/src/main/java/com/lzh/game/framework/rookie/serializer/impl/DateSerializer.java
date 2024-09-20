package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Date;

/**
 * @author zehong.l
 * @since 2024-09-13 16:41
 **/
public class DateSerializer implements Serializer<Date> {

    @Override
    public Date readObject(ByteBuf in, Class<Date> clz) {
        return new Date(ByteBufUtils.readInt64(in));
    }

    @Override
    public void writeObject(ByteBuf out, Date date) {
        ByteBufUtils.writeInt64(out, date.getTime());
    }
}
