package com.lzh.game.framework.rookie.serializer.impl;

import com.lzh.game.framework.rookie.serializer.Serializer;
import com.lzh.game.framework.rookie.utils.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.util.Calendar;

/**
 * @author zehong.l
 * @since 2024-10-23 18:17
 **/
public class CalendarSerializer implements Serializer<Calendar> {

    @Override
    public Calendar readObject(ByteBuf in, Class<Calendar> clz) {
        long time = ByteBufUtils.readInt64(in);
        var calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        return calendar;
    }

    @Override
    public void writeObject(ByteBuf out, Calendar calendar) {
        ByteBufUtils.writeInt64(out, calendar.getTimeInMillis());
    }
}
