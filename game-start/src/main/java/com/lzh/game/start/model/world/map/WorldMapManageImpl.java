package com.lzh.game.start.model.world.map;

import com.lzh.game.start.model.world.resource.MapResource;
import com.lzh.game.start.model.world.scene.WorldMapManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WorldMapManageImpl implements WorldMapManage {

    private final Map<Integer, WorldMap> worldMaps = new ConcurrentHashMap<>();

    @Override
    public WorldMap buildWorldMap(MapResource resource) {
        WorldMap map = loadWorldMap(resource);
        return map;
    }

    @Override
    public WorldMap getWoldMap(int mapId) {
        return worldMaps.get(mapId);
    }

    @Override
    public WorldMap register(WorldMap map) {
        return worldMaps.put(map.getMapId(), map);
    }

    private WorldMap loadWorldMap(MapResource resource) {
        int mapId = resource.getKey();
        WorldMap worldMap = WorldMap.of(loadMapData(resource), mapId, resource.getWidth(), resource.getLength());
        return worldMap;
    }

    private byte[][] loadMapData(MapResource mapResource) {
        return new byte[][]{};
    }

}
