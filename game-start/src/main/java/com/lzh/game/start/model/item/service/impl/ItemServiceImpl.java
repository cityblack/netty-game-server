package com.lzh.game.start.model.item.service.impl;


import com.lzh.game.framework.utils.id.IdGeneratorHelp;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.item.model.UseAbleItem;
import com.lzh.game.start.model.item.resource.ItemResource;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.start.model.player.Player;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ItemResourceManage itemResourceManage;

    @Autowired
    private PlayerBagService bagService;

    @Override
    public List<AbstractItem> createItem(int itemModel, int num) {
        ItemResource resource = getItemResourceById(itemModel);
        if (Objects.isNull(resource)) {
            return Collections.EMPTY_LIST;
        }
        int createNum = num / resource.getStack() + 1;
        List<AbstractItem> created = new ArrayList<>(createNum);
        int moreNum = num;
        for (int i = 0; i < createNum; i++) {
            created.add(create(resource, moreNum));
            moreNum -= resource.getStack();
        }
        return created;
    }

    private AbstractItem create(ItemResource resource, int num) {
        AbstractItem item = resource.getType().create();
        item.setObjectId(IdGeneratorHelp.nextId());
        item.setResourceId(resource.getKey());
        item.setNum((short) num);
        return item;
    }

    @Override
    public ItemResource getItemResourceById(int item) {
        return itemResourceManage.findItemResourceById(item);
    }

    @Override
    public void useItem(Player player, long objectId, int num, Map<String, String> params) {

//        AbstractItem item = bagService.getItemByObjectId(player, objectId);
//        checkUseItem(item);
//        UseAbleItem useAbleItem = (UseAbleItem)item;
//        useAbleItem.useVerify(player, params);
//        LogReason reason = LogReason.USE_ITEM;
//        try {
//            bagService.reduceItemById(player, objectId, num, reason);
//            useAbleItem.useEffect(player, params, reason);
//        } catch (NotEnoughItemException e) {
//            log.error("使用道具异常:", e);
//            throw new RequestException(I18n.NOT_ENOUGH_ITEMS);
//        }
    }

    private void checkUseItem(AbstractItem item) {

        if (Objects.isNull(item)) {
            throw new RequestException(I18n.ILLEGAL);
        }
        if (!(item instanceof UseAbleItem)) {
            throw new RequestException(I18n.ILLEGAL);
        }

    }
}
