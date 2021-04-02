package com.lzh.game.start.model.item.resource;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Resource;
import lombok.Data;

@Resource
@Data
public class PandoraResource {

    @Id
    private int key;
    /**
     * 0 根据权重随机抽取
     * 1 根据选择抽取
     */
    private int type;

    private PandoraModel[] item;

}
