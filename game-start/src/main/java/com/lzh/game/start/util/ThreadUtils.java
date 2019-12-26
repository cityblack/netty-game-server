package com.lzh.game.start.util;

import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.world.scene.Scene;

import java.util.function.Consumer;

public class ThreadUtils {

    public static void addTask(Player player, Consumer<Player> runnable) {

        SpringContext.singleTon().getExecutorService().submit(player.currentScene(),  s -> {
            runnable.accept(player);
            return null;
        });
    }

    public static void addTask(Scene scene, Consumer<Scene> runnable) {
        SpringContext.singleTon().getExecutorService().submit(scene,  s -> {
            runnable.accept(s);
            return null;
        });
    }

    private ThreadUtils() {}
}
