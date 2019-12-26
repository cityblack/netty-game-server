package com.lzh.game.start.model.item.bag.service;

import com.lzh.game.start.PlayerUtils;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzh.game.start.App.class})
@Slf4j
public class ItemCurdTest {

    @Autowired
    private PlayerBagService playerBagService;
    @Autowired
    private ItemService itemService;

    @Test
    public void reduceItems() {
    }

    @Test
    public void reduceItemById() {
    }

    @Test
    public void removeItem() {
    }

    @Test
    public void addItem() {
        Player player = getPlayer();
        playerBagService.addItem(player, itemService.createItem(1, 1), LogReason.CONSOLE);
        playerBagService.update(player);
        List<AbstractItem> list = playerBagService.getItemsByItemModelId(player, 1);
        list.forEach(e -> log.debug("id:{} itemModel:{} num:{}", e.getObjectId(), e.getResourceId(), e.getNum()));
    }


    private Player getPlayer() {
        return PlayerUtils.get();
    }
}