package com.lzh.game.start.model.world.scene;

import java.util.Set;
import java.util.stream.Stream;

public interface SceneManage {
    /**
     * 创建场景
     * @param mapId
     * @param channel
     * @return
     */
    Scene createScene(int mapId, int channel);

    /**
     * 自增长序号创建场景
     * @param mapId
     * @return
     */
    Scene createScene(int mapId);

    /**
     *
     * @return
     */
    Stream<Scene> getScene();

    /**
     *
     * @param key
     * @return
     */
    Scene getScene(SceneKey key);

    /**
     *
     * @param mapId
     * @return
     */
    Stream<Scene> getScenesByMap(int mapId);
    /**
     *
     * @param mapId
     * @return
     */
    int getMapSceneCount(int mapId);

    /**
     *
     * @param sceneKey
     */
    void destroyScene(SceneKey sceneKey);

    /**
     * 当前玩家数量最少的场景
     * @return
     */
    Scene minPlayerCountScene();

    /**
     * 特定地图 当前玩家数量最少的场景
     * @param mapId
     * @return
     */
    Scene minPlayerCountScene(int mapId);
}
