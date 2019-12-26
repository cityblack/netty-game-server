package com.lzh.game.start.model.world.scene.impl;

import com.lzh.game.start.model.world.Position;
import com.lzh.game.start.model.world.map.WorldMap;
import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SceneMapImpl implements SceneMap {

    private Scene parent;

    public static SceneMap of(Scene scene) {
        SceneMapImpl sceneMap = new SceneMapImpl();
        sceneMap.parent = scene;
        return sceneMap;
    }

    public WorldMap getWorldMap() {
        return parent.getWorldMap();
    }

    @Override
    public boolean isInRange(Position position) {
        WorldMap map = getWorldMap();
        return position.getX() <= map.getWidth() && position.getY() <= map.getLength();
    }

    @Override
    public boolean isBlock(Position position) {
        return getWorldMap().isBlock(position.getX(), position.getY());
    }

    @Override
    public boolean isInRangeBlock(Position position) {
        return isInRange(position) && isBlock(position);
    }

    @Override
    public boolean isInRangeNotBlock(Position position) {
        return isInRange(position) && !isBlock(position);
    }

    @Override
    public void init() {
        if (log.isInfoEnabled()) {
            log.info("Init key:{} law data", this.parent.getKey());
        }
    }

    @Override
    public void destroy() {

    }
}
