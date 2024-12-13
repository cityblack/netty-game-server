package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.model.*;
import io.netty.buffer.ByteBufAllocator;
import org.apache.fury.Fury;
import org.apache.fury.memory.Platform;
import org.junit.jupiter.api.Test;
import org.springframework.util.ReflectionUtils;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

/**
 * @author zehong.l
 * @since 2024-09-18 17:38
 **/
public class ObjectTest {

    @Test
    public void compare() {
        var rookie = new Rookie();
        var value = System.currentTimeMillis();
        var buf = ByteBufAllocator.DEFAULT.heapBuffer();
        rookie.serializer(buf, value);
        System.out.println(buf.writerIndex());

        Fury fury = Fury.builder().build();
        var bytes = fury.serialize(value);
        System.out.println(bytes.length);
    }

    @Test
    public void compareObject() {
        var rookie = new Rookie();
        rookie.register(101, OT.class);
        rookie.register(102, OtInner.class);
        // Auto register 103
        rookie.register(AbstractItem.class);
        rookie.register(105, Item.class);
        var ot = OT.createOT();
        var buf = ByteBufAllocator.DEFAULT.heapBuffer();
        rookie.serializer(buf, ot);
        System.out.println(buf.writerIndex());

        Fury fury = Fury.builder().build();
        fury.register(OT.class);
        fury.register(OtInner.class);
        fury.register(AbstractItem.class);
        fury.register(Item.class);
        var bytes = fury.serialize(ot);
        System.out.println(bytes.length);
    }

    @Test
    public void testObject() {
        var rookie = new Rookie();
        rookie.register(101, OT.class);
        rookie.register(102, OtInner.class);
        rookie.register(AbstractItem.class);
        rookie.register(103, Item.class);
        rookie.register(104, EnumOT.class);
        rookie.register(TestEnum.class);
        var ot = EnumOT.of();
        var buf = ByteBufAllocator.DEFAULT.heapBuffer();
        rookie.serializer(buf, ot);
        System.out.println(buf.writerIndex());

        var de = rookie.deserializer(buf, EnumOT.class);
        System.out.println(de);
        System.out.println(de.getOt().getItem().getIndex());

    }
}
