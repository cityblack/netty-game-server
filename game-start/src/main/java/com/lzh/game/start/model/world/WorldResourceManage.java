package com.lzh.game.start.model.world;

import com.lzh.game.resource.Static;
import com.lzh.game.resource.Storage;
import com.lzh.game.start.model.world.resource.MapResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 世界资源管理
 */
@Component
public class WorldResourceManage {

    @Static
    private Storage<Integer, MapResource> resourceStorage;

    public MapResource findMapResourceById(int mapId) {
        return resourceStorage.get(mapId);
    }

    public List<MapResource> findAllMapResource() {
        return resourceStorage.getAll();
    }

}
