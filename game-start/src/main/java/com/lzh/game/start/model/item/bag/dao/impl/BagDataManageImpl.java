package com.lzh.game.start.model.item.bag.dao.impl;

import com.lzh.game.repository.CacheDataRepository;
import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.item.bag.dao.BagRepository;
import com.lzh.game.start.model.player.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BagDataManageImpl implements BagDataManage {

    @Autowired
    private BagRepository bagRepository;

    @Autowired
    private CacheDataRepository cacheDataRepository;

    @Override
    public Bag findBagByPlayer(Player player) {
        return cacheDataRepository.enhanceLoadOrCreate(player.getObjectId(), Bag.class, bagRepository, Bag::of);
    }

    @Override
    public void updateBag(Bag bag) {
        cacheDataRepository.update(bag);
    }
}
