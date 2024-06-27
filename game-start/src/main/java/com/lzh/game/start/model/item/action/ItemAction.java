package com.lzh.game.start.model.item.action;

import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.framework.socket.starter.anno.Action;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

@Action
@Slf4j
public class ItemAction {

    @Autowired
    private ItemService itemService;

}
