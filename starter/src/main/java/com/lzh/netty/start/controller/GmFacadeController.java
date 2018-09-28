package com.lzh.netty.start.controller;

import com.lzh.netty.framework.core.player.Player;
import com.lzh.netty.start.gm.annotation.GmFacade;
/**
 * Gm facade example
 * The method first arg must be {@link Player}, and you can find the method by method name with args cont
 * Executing the method is not depend on the args type while depend on the args count. So, if you gonna to use the same
 * method signature, that are different with just the arg count more or less {@link DefaultGmServiceImpl}
 * The method args type just support Integer/int, Float/float, Boolean/boolean, Long/long
 * for example:
 *  test(int id) -- is wrong
 *  test(Play play, int id) -- is right
 *  test(Play play, int id) test(Play play, float id) -- is wrong
 *  test(Play play, int id) test(Play play, int id, int age) -- is right
 *
 */
@GmFacade
public class GmFacadeController {

    public void test(Player player, int id) {
        System.out.println("this is gm test " + id);
    }
}
