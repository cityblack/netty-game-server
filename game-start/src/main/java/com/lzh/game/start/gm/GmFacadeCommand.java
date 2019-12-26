package com.lzh.game.start.gm;

import com.lzh.game.start.gm.service.DefaultGmServiceImpl;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.item.model.AbstractItem;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.SpringContext;

import java.util.List;

/**
 * {@link com.lzh.game.start.gm.service.GmHandlerMethod}
 * Gm facade example
 * The convent first arg must be {@link com.lzh.game.start.model.player.Player}, and you can find the convent by convent name with args cont
 * Executing the convent is not depend on the args type while depend on the args count. So, if you gonna to use the same
 * convent signature, that are different with just the arg count more or less {@link DefaultGmServiceImpl}
 * The convent args type just support:
 *      Integer/int/Integer[]/int[],
 *      Float/float/Float[]/float[],
 *      Boolean/boolean/Boolean[]/boolean[],
 *      Long/long/Long[]/long[]
 * for example:
 *  test(int id) -- wrong
 *  test(Play play, int id) -- right
 *  test(Play play, int id) test(Play play, float id) -- wrong
 *  test(Play play, int id) test(Play play, int id, int age) -- right
 *
 */
@GmFacade
public class GmFacadeCommand {
    /**
     * 添加物品
     * @param player
     * @param param 第一个参数为itemModel 第二个参数可选 为道具数量
     */
    public void addItem(Player player, int[] param) {
        if (param.length < 1) {
            return;
        }
        int itemModelId = param[0];
        int num = param.length >= 2 ? param[1] : 1;

        List<AbstractItem> items = getContext().getItemService().createItem(itemModelId, num);
        getContext().getBagService().addItem(player, items, LogReason.CONSOLE);
    }

    private SpringContext getContext() {
        return SpringContext.singleTon();
    }
}
