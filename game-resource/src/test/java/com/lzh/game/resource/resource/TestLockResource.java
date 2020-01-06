package com.lzh.game.resource.resource;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Resource;
import lombok.Data;

@Resource
@Data
public class TestLockResource {
    @Id
    private int id;

    private String name;

    private boolean lock;
}
