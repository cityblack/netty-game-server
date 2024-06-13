package com.lzh.game.start.model.item.service;

import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.resource.PandoraResource;
import com.lzh.game.framework.resource.Static;
import com.lzh.game.framework.resource.Storage;
import org.springframework.stereotype.Component;

@Component
public class ItemResourceManage {

    @Static
    private Storage<Integer, ItemResource> itemResourceStorage;

    @Static
    private Storage<Integer, PandoraResource> pandoraResourceStorage;

    public ItemResource findItemResourceById(int item) {
        return itemResourceStorage.getOrThrow(item);
    }

    public PandoraResource findPandoraResourceById(int id) {
        return pandoraResourceStorage.getOrThrow(id);
    }
}
