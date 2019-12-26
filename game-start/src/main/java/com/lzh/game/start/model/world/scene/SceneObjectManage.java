package com.lzh.game.start.model.world.scene;

import com.lzh.game.start.model.game.GameObjectType;
import com.lzh.game.start.model.game.SceneObject;

import java.util.stream.Stream;

/**
 * In scene's objects
 */
public interface SceneObjectManage {

    void add(SceneObject object);

    void remove(SceneObject object);

    SceneObject getObject(long objectId, GameObjectType objectType);

    Stream<SceneObject> getObject(GameObjectType type);

    Stream<SceneObject> getAll();

    int numOfType(GameObjectType type);

    boolean containObject(long objectId, GameObjectType objectType);
}
