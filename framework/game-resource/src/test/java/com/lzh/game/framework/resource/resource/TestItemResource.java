package com.lzh.game.framework.resource.resource;

import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Index;
import com.lzh.game.framework.resource.Resource;
import lombok.Data;

@Resource
@Data
public class TestItemResource {

    public static final String INDEX = "index";

    @Id
    private int key;

    private String name;

    private int type;

    private boolean pile;

    @Index(INDEX)
    public String getIndex() {
        return name + type;
    }
}
