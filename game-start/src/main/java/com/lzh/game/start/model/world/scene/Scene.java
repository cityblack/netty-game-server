package com.lzh.game.start.model.world.scene;

import com.lzh.game.start.util.RandomUtils;
import com.lzh.game.start.model.world.Position;
import com.lzh.game.start.model.world.map.WorldMap;
import com.lzh.game.start.model.world.scene.impl.SceneMapImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Objects;

/**
 * 考虑一个地图对应多个场景
 * 由场景id或者地图和序号{@link #key}组成唯一
 */
@Slf4j
public abstract class Scene {

    @Getter
    protected SceneKey key;

    @Getter
    protected long createTime;

    @Getter
    protected WorldMap worldMap;

    @Getter
    @Setter
    protected SceneMap sceneMap;

    @Getter
    @Setter
    protected SceneObjectManage objectManage;

    public void destroy() {
        if (log.isInfoEnabled()) {
            log.info("Destroy scene:{} ", this.getKey());
        }
        this.getSceneMap().destroy();
    }

    public SceneMap getSceneMap() {
        if (Objects.isNull(sceneMap)) {
            sceneMap = SceneMapImpl.of(this);
        }
        return sceneMap;
    }

    /**
     * Scene active indicators
     * @return
     */
    public int loadFactor() {
        return 0;
    }

    public int getMapId() {
        return worldMap.getMapId();
    }

    public Scene(int mapId, int channel, WorldMap worldMap) {
        this.key = SceneKey.of(mapId, channel);
        this.worldMap = worldMap;
    }

    public abstract void init();
}
