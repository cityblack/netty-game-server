package com.lzh.game.start.model.world.scene;

import com.lzh.game.start.model.world.map.WorldMap;
import com.lzh.game.start.model.world.resource.MapResource;

public interface WorldMapManage {

    WorldMap buildWorldMap(MapResource resource);

    WorldMap getWoldMap(int mapId);

    WorldMap register(WorldMap map);
}
