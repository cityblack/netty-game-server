package com.lzh.game.start.model.item.service;

import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.player.Player;

import java.util.List;
import java.util.Map;

public interface ItemService {

    List<AbstractItem> createItem(int itemModel, int num);

    ItemResource getItemResourceById(int item);

    void useItem(Player player, long objectId, int num, Map<String, String> params);
}
