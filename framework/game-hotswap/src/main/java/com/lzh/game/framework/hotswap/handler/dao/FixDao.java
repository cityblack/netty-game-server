package com.lzh.game.framework.hotswap.handler.dao;

import java.util.List;

/**
 * @author zehong.l
 * @since 2024-10-08 12:11
 **/
public interface FixDao {

    List<String> loadAllUsedId();

    void saveFix(String id);
}
