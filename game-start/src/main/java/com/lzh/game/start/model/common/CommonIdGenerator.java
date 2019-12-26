package com.lzh.game.start.model.common;

import com.lzh.game.start.model.common.model.impl.SceneData;

public enum CommonIdGenerator {
    /**
     * 生成的场景数据
     */
    SCENE_DATA(1001) {
        @Override
        public SceneData of() {
            return SceneData.of();
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
