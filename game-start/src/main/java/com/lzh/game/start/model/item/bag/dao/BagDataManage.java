package com.lzh.game.start.model.item.bag.dao;

import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.player.Player;

public interface BagDataManage {

    Bag findBagByPlayer(Player player);

    void updateBag(Bag bag);
}
