package com.lzh.game.common;

import com.lzh.game.common.util.SnowFlake;
import org.junit.Test;

import java.util.stream.IntStream;

public class SnowFlakeTest {

    @Test
    public void nextId() {
        SnowFlake snowFlake = new SnowFlake();
        IntStream.range(0, 1000).forEach(e -> System.out.println(snowFlake.nextId()));
    }
}