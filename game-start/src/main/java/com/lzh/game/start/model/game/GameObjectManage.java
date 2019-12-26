package com.lzh.game.start.model.game;

/**
 * Game objects manager
 */
public interface GameObjectManage<T extends GameObject> {

    T findVisible(long objectId);

    GameObjectType type();
}
