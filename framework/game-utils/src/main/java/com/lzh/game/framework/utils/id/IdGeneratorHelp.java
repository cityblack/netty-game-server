package com.lzh.game.framework.utils.id;

import java.util.Objects;

/**
 * Id生成器
 */
public class IdGeneratorHelp {

    private static IdGenerator idGenerator;

    private IdGeneratorHelp() {}

    public static void setIdGenerator(IdGenerator id) {
        synchronized (IdGeneratorHelp.class) {
            idGenerator = id;
        }
    }

    public static long nextId() {
        if (Objects.isNull(idGenerator)) {
            synchronized (IdGeneratorHelp.class) {
                if (Objects.isNull(idGenerator)) {
                    idGenerator = new SnowflakeIdGenerator(1);
                }
            }
        }
        return idGenerator.nextId();
    }
}
