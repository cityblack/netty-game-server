package com.lzh.game.start.util;

import com.lzh.game.common.event.EventBus;
import com.lzh.game.framework.scheduler.SchedulerOption;
import com.lzh.game.repository.CacheDataRepository;
import com.lzh.game.socket.dispatcher.mapping.CmdMappingManage;
import com.lzh.game.start.model.item.bag.dao.BagDataManage;
import com.lzh.game.start.model.item.bag.service.PlayerBagService;
import com.lzh.game.start.model.common.service.CommonDataManage;
import com.lzh.game.start.model.item.service.ItemResourceManage;
import com.lzh.game.start.model.item.service.ItemService;
import com.lzh.game.start.model.player.service.PlayerManage;
import com.lzh.game.start.model.player.service.PlayerService;
import com.lzh.game.start.model.game.GameObjectManageFactory;
import com.lzh.game.start.model.player.service.PlayerSessionManage;
import com.lzh.game.start.model.player.service.impl.PlayerExtService;
import com.lzh.game.start.model.target.handler.TargetHandlerManage;
import com.lzh.game.start.model.target.model.TargetModelManage;
import com.lzh.game.start.model.wallet.service.WalletService;
import com.lzh.game.start.model.world.WorldResourceManage;
import com.lzh.game.start.model.world.WorldService;
import com.lzh.game.start.model.world.scene.SceneManage;
import com.lzh.game.start.pool.GameExecutorService;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SpringContext implements ApplicationContextAware {

    @Autowired
    private PlayerManage playerManage;

    public PlayerManage getPlayerManage() {
        return playerManage;
    }

    @Autowired
    private PlayerSessionManage playerSessionManage;

    public PlayerSessionManage getPlayerSessionManage() {
        return playerSessionManage;
    }

    @Autowired
    private PlayerService playerService;

    public PlayerService getPlayerService() {
        return playerService;
    }

    @Autowired
    private WorldService worldService;

    public WorldService getWorldService() {
        return worldService;
    }

    @Autowired
    private GameObjectManageFactory gameObjectManageFactory;

    public GameObjectManageFactory getGameObjectManageFactory() {
        return gameObjectManageFactory;
    }

    @Autowired
    private CacheDataRepository cacheDataRepository;

    public CacheDataRepository getCacheDataRepository() {
        return cacheDataRepository;
    }

    @Autowired
    private CmdMappingManage cmdMappingManage;

    public CmdMappingManage getCmdMappingManage() {
        return cmdMappingManage;
    }

    @Autowired
    private WalletService walletService;

    public WalletService getWalletService() {
        return walletService;
    }

    @Autowired
    private CommonDataManage commonDataManage;

    public CommonDataManage getCommonDataManage() {
        return commonDataManage;
    }

    @Autowired
    private WorldResourceManage worldResourceManage;

    public WorldResourceManage getWorldResourceManage() {
        return worldResourceManage;
    }

    @Autowired
    private SceneManage sceneManage;

    public SceneManage getSceneManage() {
        return sceneManage;
    }


    @Autowired
    private RedissonClient redissonClient;

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    @Autowired
    private GameExecutorService executorService;

    public GameExecutorService getExecutorService() {
        return executorService;
    }

    @Autowired
    private PlayerBagService bagService;

    public PlayerBagService getBagService() {
        return bagService;
    }

    @Autowired
    private ItemResourceManage itemResourceManage;

    public ItemResourceManage getItemResourceManage() {
        return itemResourceManage;
    }

    @Autowired
    private BagDataManage bagDataManage;

    public BagDataManage getBagDataManage() {
        return bagDataManage;
    }

    @Autowired
    private TargetModelManage targetModelManage;

    public TargetModelManage getTargetModelManage() {
        return targetModelManage;
    }

    @Autowired
    private TargetHandlerManage targetHandlerManage;

    public TargetHandlerManage getTargetHandlerManage() {
        return targetHandlerManage;
    }

    @Autowired
    private EventBus eventBus;

    public EventBus getEventBus() {
        return eventBus;
    }

    @Autowired
    private ItemService itemService;

    public ItemService getItemService() {
        return itemService;
    }

    @Autowired
    private PlayerExtService playerExtService;

    public PlayerExtService getPlayerExtService() {
        return playerExtService;
    }

    @Autowired
    private SchedulerOption schedulerOption;

    public SchedulerOption getSchedulerOption() {
        return schedulerOption;
    }

    // ======= init ======
    private ApplicationContext context;

    public ApplicationContext getContext() {
        return singleTon().context;
    }

    private static SpringContext instance;

    public static SpringContext singleTon() {
        return instance;
    }

    @PostConstruct
    private final void init() {
        instance = this;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
}
