package com.lzh.game.framework.rookie;

import com.lzh.game.framework.rookie.model.*;
import io.netty.buffer.ByteBufAllocator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.fury.Fury;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        rookie.register((short) 1001, OT.class);
        rookie.register((short) 1002, OtInner.class);
        rookie.register((short) 1003, AbstractItem.class);
        rookie.register((short) 1004, Item.class);
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
        rookie.register((short) 1001, OT.class);
        rookie.register((short) 1002, OtInner.class);
        rookie.register((short) 1003, AbstractItem.class);
        rookie.register((short) 1004, Item.class);
        rookie.register((short) 1005, EnumOT.class);
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
