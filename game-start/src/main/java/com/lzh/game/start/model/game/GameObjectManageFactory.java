package com.lzh.game.start.model.game;

public interface GameObjectManageFactory {

    GameObjectManage getVisibleManage(GameObjectType objectType);
}
