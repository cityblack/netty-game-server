package com.lzh.game.resource.resource;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Index;
import com.lzh.game.resource.Resource;
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
