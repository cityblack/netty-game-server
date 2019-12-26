package com.lzh.game.start.model.world.resource;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Resource;
import lombok.Data;

@Data
@Resource
public class MapResource {

    @Id
    private int key;

    private String name;

    private String resourcePath;

    private int width;

    private int length;

}
