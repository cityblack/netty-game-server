package com.lzh.game.business.utils;

/**
 * Id生成器
 */
public class IdGenerator {

    private static IdGenerator idGenerator = new IdGenerator();

    private SnowFlake snowFlake = new SnowFlake();

    private IdGenerator() {}

    public static IdGenerator singleton() {
        return idGenerator;
    }

    public long createLongId() {
        return snowFlake.nextId();
    }
}
