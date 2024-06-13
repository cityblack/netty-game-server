package com.lzh.game.framework.resource.resource;

import com.lzh.game.framework.resource.Id;
import com.lzh.game.framework.resource.Resource;
import lombok.Data;

@Resource
@Data
public class TestThirdLockResource {
    @Id
    private int id;

    private String name;

    private int order;
}
