package com.lzh.game.start.model.player.resource;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Index;
import com.lzh.game.resource.Resource;
import lombok.Data;

@Resource
@Data
public class PlayerLevelResource {

    public static final String LEVEL_INDEX = "index_level";

    @Id
    private int key;

    @Index(value = LEVEL_INDEX, unique = true)
    private int level;

    private long exp;
}
