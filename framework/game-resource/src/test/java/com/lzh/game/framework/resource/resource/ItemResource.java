package com.lzh.game.framework.resource.resource;

import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Index;
import com.lzh.game.framework.resource.Resource;
import com.lzh.game.framework.resource.data.DictResource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Resource
@Data
@Slf4j
public class ItemResource implements DictResource {

    public static final String INDEX = "index";

    public static final String TYPE_INDEX = "type_index";

    @Id
    private int key;

    private String name;

    @Index(TYPE_INDEX)
    private String type;

    private boolean pile;

    @Index(INDEX)
    public String getIndex() {
        return name + type;
    }

    @Override
    public void dict(String key, Object value) {
        log.info("dict data: {}-{}", key, value);
    }
}
