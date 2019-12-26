package com.lzh.game.start.model.world;

import com.lzh.game.start.model.world.map.WorldMap;
import com.lzh.game.start.model.world.resource.MapResource;
import com.lzh.game.start.model.world.scene.SceneManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class World {

    @Autowired
    private SceneManage sceneManage;



}
