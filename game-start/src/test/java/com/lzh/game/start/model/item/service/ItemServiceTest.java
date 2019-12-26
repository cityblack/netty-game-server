package com.lzh.game.start.model.item.service;

import com.lzh.game.start.PlayerUtils;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzh.game.start.App.class})
@Slf4j
public class ItemServiceTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private PlayerBagService bagService;

    @Test
    public void createItem() {
    }

    @Test
    public void useItem() {
        Player player = PlayerUtils.get();
        clearPack(player);
        // 潘多拉
        List<AbstractItem> list = itemService.createItem(2, 1);
        bagService.addItem(player, list, LogReason.CONSOLE);
        long itemId = list.get(0).getObjectId();
        itemService.useItem(player, itemId, 1, new HashMap<>());
        player.getBag().getPack().loadedItems().forEach((k,v) -> log.debug("item:{} num:{}", v.getResource().getName(), v.getNum()));
        List<AbstractItem> items = bagService.getItemsByItemModelId(player, 5);
        if (!items.isEmpty()) {
            AbstractItem i = items.get(0);
            itemService.useItem(player, i.getObjectId(), i.getNum(), new HashMap<>());

        }
    }

    private void clearPack(Player player) {
        player.getBag().getPack().loadedItems().keySet().forEach(e  -> player.getBag().getPack().removeItem(e));
    }
}