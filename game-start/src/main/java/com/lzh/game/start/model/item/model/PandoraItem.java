package com.lzh.game.start.model.item.model;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.item.resource.PandoraModel;
import com.lzh.game.start.model.item.resource.PandoraResource;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.RandomUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 潘多拉
 */
@Slf4j
public class PandoraItem extends UseAbleItem {

    @Override
    public void useVerify(Player player, Map<String, String> params) {
        super.useVerify(player, params);
        PandoraResource resource = pandoraResource();
        if (isSelectPandora(resource) && !params.containsKey(selectParamKey())) {
            throw new RequestException(I18n.ILLEGAL);
        }
        verifyFreeSpace(player, resource);
    }

    /**
     * 验证玩家背包空间
     * @param player
     * @param resource
     */
    private void verifyFreeSpace(Player player, PandoraResource resource) {

        PandoraModel[] models = resource.getItem();
        for (PandoraModel model: models) {
            Map<Integer, Integer> items = new HashMap<>(model.getItem().length);
            for (PandoraModel.PandoraModelInner inner: model.getItem()) {
                items.put(inner.getItemId(), inner.getNum());
            }
            ApplicationUtils.getBean(PlayerBagService.class).isEnoughGrid(player, items);
        }
    }

    @Override
    public void useEffect(Player player, Map<String, String> params, LogReason logReason) {
        super.useEffect(player, params, logReason);
        PandoraResource resource = pandoraResource();
        List<AbstractItem> items = openPandora(resource, params);
        ApplicationUtils.getBean(PlayerBagService.class).addItem(player, items, logReason);
    }

    private PandoraResource pandoraResource() {
        return ApplicationUtils.getBean(ItemResourceManage.class).findPandoraResourceById(getResourceId());
    }

    private List<AbstractItem> openPandora(PandoraResource resource, Map<String, String> params) {

        if (isRandomPandora(resource)) {
            PandoraModel selected = RandomUtils.enhanceRandList(Arrays.asList(resource.getItem()), model -> model.getWeight());
            if (log.isDebugEnabled()) {
                log.debug("Pandora抽中:{}", selected);
            }
            return modelToItem(selected);

        } else {
            int selectIndex = Integer.parseInt(params.getOrDefault(selectParamKey(), "0"));
            PandoraModel selected = resource.getItem()[selectIndex];
            if (log.isDebugEnabled()) {
                log.debug("Pandora选中:{}", selected);
            }
            return modelToItem(selected);
        }
    }

    private List<AbstractItem> modelToItem(PandoraModel selected) {
        return Stream.of(selected.getItem())
                .flatMap(e -> ApplicationUtils.getBean(ItemService.class).createItem(e.getItemId(), e.getNum()).stream())
                .collect(Collectors.toList());
    }

    private boolean isRandomPandora(PandoraResource resource) {
        return resource.getType() == 0;
    }

    private boolean isSelectPandora(PandoraResource resource) {
        return resource.getType() == 1;
    }

    private String selectParamKey() {
        return "select";
    }
}
