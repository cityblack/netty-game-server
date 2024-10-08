package com.lzh.game.framework.hotswap.handler;

/**
 * @author zehong.l
 * @since 2024-10-08 11:31
 **/
public interface FixHandler {

    /**
     * execute
     *
     */
    void fix();

    /**
     * Handler unique id
     * Recommended: YYYY MMM DD HH +model.
     *  -- 2024100812Fix
     * @return id
     */
    String getId();

    /**
     * Has it been executed
     *
     * @return executed status
     */
    boolean executed();
}
