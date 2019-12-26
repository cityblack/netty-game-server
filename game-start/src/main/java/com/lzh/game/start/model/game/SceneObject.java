package com.lzh.game.start.model.game;

import com.lzh.game.start.model.world.Position;
import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneKey;
import lombok.Getter;
import lombok.Setter;

public abstract class SceneObject extends GameObject {

    @Getter
    @Setter
    private SceneKey sceneKey;

    @Getter
    @Setter
    private Position position;

    public void updatePosition(Scene scene, Position position) {
        this.sceneKey = scene.getKey();
        this.position = position;
    }
}
