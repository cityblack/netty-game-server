package com.lzh.game.start.model.world.scene;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

@ToString
public class SceneKey implements Serializable {

    private static final long serialVersionUID = 8092239108787581431L;

    @Getter
    private int mapId;

    @Getter
    private int channel;

    public static SceneKey of(int mapId, int channel) {
        SceneKey sceneKey = new SceneKey();
        sceneKey.mapId = mapId;
        sceneKey.channel = channel;
        return sceneKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SceneKey sceneKey = (SceneKey) o;
        return mapId == sceneKey.mapId &&
                channel == sceneKey.channel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapId, channel);
    }
}
