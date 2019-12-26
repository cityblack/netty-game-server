package com.lzh.game.start.pool;

import com.lzh.game.socket.dispatcher.ExchangeProcess;
import com.lzh.game.socket.dispatcher.ServerExchange;
import com.lzh.game.start.cmd.CmdMessage;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.world.scene.SceneKey;
import com.lzh.game.start.util.SpringContext;
import com.lzh.game.start.model.world.scene.Scene;
import com.lzh.game.start.model.world.scene.SceneManage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 将场景与线程绑定
 * 按玩家所在场景执行玩家操作
 */
@Slf4j
public class DefaultBusinessThreadPool implements ExchangeProcess, GameExecutorService, InitializingBean {

    private ExecutorService[] executors;

    private int available;
    // 将登陆绑死线程
    private final static int LOGIN_PROCESS = 0;

    private SceneManage sceneManage;

    private static Set<Integer> specialCmd;

    static {
          specialCmd = new HashSet<>();
          specialCmd.add(CmdMessage.CM_LOGIN);
          specialCmd.add(CmdMessage.CM_REGISTER);
    }

    /**
     * 根据场景负载 绑定线程
     * <场景标识, 线程序号>
     */
    private Map<SceneKey, Integer> sceneBindIndex = new ConcurrentHashMap<>();

    public DefaultBusinessThreadPool(SceneManage sceneManage) {
        this.sceneManage = sceneManage;
    }

    @Override
    public <T> CompletableFuture<T> addRequestProcess(ServerExchange exchange, Function<ServerExchange, T> function) {
        if (specialRequest(exchange.getRequest().header().getCmd())) {
            return doProcess(LOGIN_PROCESS, exchange, function);
        }
        return doProcess(index(exchange), exchange, function);
    }

    private boolean specialRequest(int cmd) {
        return specialCmd.contains(cmd);
    }

    private <T> CompletableFuture<T> doProcess(int hit, ServerExchange exchange, Function<ServerExchange, T> function) {
        return CompletableFuture.supplyAsync(() -> function.apply(exchange), executors[hit]);
    }

    private int index(ServerExchange exchange) {
        Player player = SpringContext.singleTon().getPlayerService().getPlayer(exchange.getSession());
        if (Objects.isNull(player)) {
            return LOGIN_PROCESS;
        }
        Scene scene = player.currentScene();
        return getSceneBind(scene);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        init();
    }

    private void init() {
        available = Runtime.getRuntime().availableProcessors() * 2;
        executors = IntStream.of(available)
                .mapToObj(e -> Executors.newSingleThreadScheduledExecutor())
                .toArray(ExecutorService[]::new);
        if (log.isInfoEnabled()) {
            log.info("Opens the {} core threads..", available);
        }
    }

    private int getSceneBind(Scene scene) {

        return sceneBindIndex.getOrDefault(scene.getKey(), sceneBindIndex(scene));
    }

    private int sceneBindIndex(Scene scene) {

        synchronized (scene) {
            SceneKey key = scene.getKey();
            if (sceneBindIndex.containsKey(key)) {
                return sceneBindIndex.get(key);
            }
            int minLoadThreadIndex = minLoadThread();
            if (log.isInfoEnabled()) {
                log.info("scene:{} bind to index:{} ..", key, minLoadThreadIndex);
            }
            sceneBindIndex.put(key, minLoadThreadIndex);
            return minLoadThreadIndex;
        }

    }

    private int minLoadThread() {
        Integer[] threadLoadFactor = new Integer[available];
        sceneBindIndex.forEach((k,v) -> {
            Scene scene = sceneManage.getScene(k);
            threadLoadFactor[v] += scene.loadFactor();
        });
        return Stream.of(threadLoadFactor)
                .min(Comparator.comparingInt(e -> e))
                .orElse(0);
    }

    @Override
    public <T> CompletableFuture<T> submit(Scene scene, Function<Scene, T> function) {
        return CompletableFuture.supplyAsync(() -> function.apply(scene), executors[getSceneBind(scene)]);
    }

    @Override
    public <T> CompletableFuture<T> submit(Long key, Function<Long, T> function) {
        int mod = (int) (Math.abs(key) / available);
        return CompletableFuture.supplyAsync(() -> function.apply(key), executors[mod]);
    }
}
