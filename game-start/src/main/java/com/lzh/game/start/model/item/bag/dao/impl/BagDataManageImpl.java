package com.lzh.game.start.model.item.bag.dao.impl;

import com.lzh.game.framework.repository.DataRepository;
import com.lzh.game.framework.repository.Repository;
import com.lzh.game.start.model.item.bag.Bag;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.player.Player;
import org.springframework.stereotype.Component;

@Component
public class BagDataManageImpl implements BagDataManage {

    @Repository
    private DataRepository<Long, Bag> dataRepository;

    @Override
    public Bag findBagByPlayer(Player player) {
        return dataRepository.loadOrCreate(player.getKey(), Bag::of);
    }

    @Override
    public void updateBag(Bag bag) {
        dataRepository.save(bag);
    }
}
