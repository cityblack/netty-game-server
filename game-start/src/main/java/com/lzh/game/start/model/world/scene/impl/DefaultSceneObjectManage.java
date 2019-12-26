package com.lzh.game.start.model.world.scene.impl;

import com.lzh.game.start.model.game.GameObjectManage;
import com.lzh.game.start.model.game.GameObjectManageFactory;
import com.lzh.game.start.model.game.GameObjectType;
import com.lzh.game.start.model.game.SceneObject;
import com.lzh.game.start.model.world.scene.SceneObjectManage;

import java.util.*;
import java.util.stream.Stream;

/**
 *
 */
public class DefaultSceneObjectManage implements SceneObjectManage {

    private Map<GameObjectType, Set<Long>> typeManage = new HashMap<>();

    private GameObjectManageFactory objectManageFactory;

    @Override
    public void add(SceneObject object) {
        addObject(object);
    }

    @Override
    public void remove(SceneObject object) {
        removeObject(object);
    }

    @Override
    public SceneObject getObject(long objectId, GameObjectType objectType) {
        if (!containObject(objectId, objectType)) {
            return null;
        }
        return (SceneObject) getObjectManage(objectType).findVisible(objectId);
    }


    @Override
    public Stream<SceneObject> getObject(GameObjectType type) {
        return typeManage.getOrDefault(type, new HashSet<>(0))
                .stream()
                .map(e -> (SceneObject)this.getObjectManage(type).findVisible(e));
    }

    @Override
    public Stream<SceneObject> getAll() {
        Stream.Builder<Stream<SceneObject>> builder = Stream.builder();
        typeManage.forEach((k,v) -> builder.add(v.stream().map(e -> (SceneObject)this.getObjectManage(k).findVisible(e))));
        return builder.build().flatMap(e -> e);
    }

    @Override
    public int numOfType(GameObjectType type) {
        return typeManage.getOrDefault(type, new HashSet<>(0)).size();
    }

    @Override
    public boolean containObject(long objectId, GameObjectType objectType) {

        return typeManage.getOrDefault(objectType, new HashSet<>(0)).contains(objectId);
    }

    private void addObject(SceneObject object) {
        GameObjectType type = object.objectType();
        Set<Long> ids = this.typeManage.get(type);
        if (Objects.isNull(ids)) {
            ids = this.typeManage.put(type, new HashSet<>());
        }
        ids.add(object.getObjectId());
    }

    private void removeObject(SceneObject object) {
        if (Objects.nonNull(object)) {
            this.typeManage.get(object.objectType()).remove(object.getObjectId());
        }
    }

    private GameObjectManage getObjectManage(GameObjectType type) {
        return objectManageFactory.getVisibleManage(type);
    }
}
