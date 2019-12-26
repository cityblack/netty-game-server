package com.lzh.game.start.model.common.model.impl;

import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneKey;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class SceneData {
    /**
     * 已经生成的场景数据
     * {@link Scene#getKey()}
     */
    private Set<SceneKey> scenes;

    public static SceneData of() {
        SceneData data = new SceneData();
        data.scenes = new HashSet<>();
        return data;
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene.getKey());
    }
}
