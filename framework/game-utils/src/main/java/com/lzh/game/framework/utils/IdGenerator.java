package com.lzh.game.framework.utils;

/**
 * Id生成器
 */
public class IdGenerator {

    private static final IdGenerator idGenerator = new IdGenerator();

    private final SnowFlake snowFlake = new SnowFlake();

    private IdGenerator() {}

    public static IdGenerator singleton() {
        return idGenerator;
    }

    public long createLongId() {
        return snowFlake.nextId();
    }
}
