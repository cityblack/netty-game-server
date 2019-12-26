package com.lzh.game.start.model.world.scene.impl;

import com.lzh.game.start.model.common.CommonIdGenerator;
import com.lzh.game.start.model.common.model.impl.SceneData;
import com.lzh.game.start.model.common.service.CommonDataManage;
import com.lzh.game.start.model.world.map.WorldMap;
import com.lzh.game.start.model.world.scene.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Component
@Slf4j
public class DefaultSceneManage implements SceneManage {

    @Autowired
    private WorldMapManage mapManage;

    @Autowired
    private CommonDataManage commonDataManage;
    /**
     * Scene manage <Map id, <map channel , scene>>
     */
    private Map<Integer, Map<Integer, Scene>> scenes = new ConcurrentHashMap<>();

    @Override
    public Scene createScene(int mapId, int channel) {

        if (Objects.nonNull(findScene(mapId, channel))) {
            throw new IllegalArgumentException(String.format("mapId:%d channel:%d is already exist.", mapId, channel));
        }

        Scene scene = buildScene(findWorldMap(mapId), channel);
        putScene(scene, channel);

        SceneData data = (SceneData) commonDataManage.getCommon(CommonIdGenerator.SCENE_DATA);
        if (!data.getScenes().contains(scene.getKey())) {
            if (log.isInfoEnabled()) {
                log.info("Add new scene key to common data. key:{}", scene.getKey());
            }
            data.addScene(scene);
            commonDataManage.updateCommonData(CommonIdGenerator.SCENE_DATA, data);
        }
        if (log.isInfoEnabled()) {
            log.info("Produce scene:{}", scene.getKey());
        }
        return scene;
    }

    @Override
    public Scene createScene(int mapId) {
        // Usually not destroyed after scene is created
        int nextOrder = this.scenes.getOrDefault(findWorldMap(mapId), new HashMap<>(0)).size() + 1;

        return createScene(mapId, nextOrder);
    }

    @Override
    public Stream<Scene> getScene() {
        return scenes.values().stream().flatMap(e -> e.values().stream());
    }

    @Override
    public Scene getScene(SceneKey key) {
        return findScene(key.getMapId(), key.getChannel());
    }

    @Override
    public Stream<Scene> getScenesByMap(int mapId) {
        return scenes.getOrDefault(mapId, new HashMap<>(0)).values().stream();
    }

    private Scene findScene(int mapId, int channel) {
        return scenes.get(mapId).get(channel);
    }

    @Override
    public int getMapSceneCount(int mapId) {
        return scenes.getOrDefault(mapId, new HashMap<>()).size();
    }

    @Override
    public void destroyScene(SceneKey sceneKey) {
        Scene scene = this.getScene(sceneKey);
        if (Objects.nonNull(scene)) {
            scene.destroy();
        }
    }

    @Override
    public Scene minPlayerCountScene() {

        return this.scenes.values()
                .stream()
                .flatMap(e -> e.values().stream())
                .min(Comparator.comparingInt(Scene::loadFactor))
                .orElseGet(null);
    }

    @Override
    public Scene minPlayerCountScene(int mapId) {
        return this.scenes.getOrDefault(mapId, new HashMap<>(0))
                .values()
                .stream()
                .min(Comparator.comparingInt(Scene::loadFactor))
                .orElseGet(null);
    }


    private Scene buildScene(WorldMap worldMap, int channel) {

        return null;
    }

    private boolean putScene(Scene scene, int channel) {
        int mapId = scene.getMapId();
        if (!scenes.containsKey(scene.getMapId())) {
            scenes.put(mapId, new ConcurrentHashMap<>());
        }
        scenes.get(mapId).put(channel, scene);

        return true;
    }

    private WorldMap findWorldMap(int mapId) {
        return mapManage.getWoldMap(mapId);
    }


}
