package com.lzh.game.framework.resource.resource;

import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Index;
import com.lzh.game.framework.resource.Resource;
import lombok.Data;

@Resource
@Data
public class ItemResource {

    public static final String INDEX = "index";

    @Id
    private int key;

    private String name;

    private String type;

    private boolean pile;

    private float[] size;

    @Index(INDEX)
    public String getIndex() {
        return name + type;
    }
}
