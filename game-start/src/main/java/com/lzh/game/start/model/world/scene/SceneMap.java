package com.lzh.game.start.model.world.scene;

import com.lzh.game.start.model.world.Position;


/**
 * Scene map. 主要管理场景中的地图信息
 */
public interface SceneMap {
    /**
     * 是否在地图范围内 主要用于边界判断
     * @param position
     * @return
     */
    boolean isInRange(Position position);
    /**
     * 是否是阻挡物
     * @param position
     * @return
     */
    boolean isBlock(Position position);

    /**
     * 是否是在地图范围内的阻挡
     * @param position
     * @return
     */
    boolean isInRangeBlock(Position position);

    /**
     * 是否是在地图范围内的非阻挡
     * @param position
     * @return
     */
    boolean isInRangeNotBlock(Position position);

    /**
     *
     */
    void init();

    /**
     * 销毁
     */
    void destroy();
}
