package com.lzh.game.start.model.common;

public enum CommonIdGenerator {
    /**
     * 生成的场景数据
     */
    DEMO(1001) {
        @Override
        public Object of() {
            return null;
        }
    },
    ;
    private int id;

    public abstract Object of();

    CommonIdGenerator(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
