package com.lzh.game.framework.repository.persist;

/**
 * @author zehong.l
 * @since 2024-10-12 15:46
 **/
public interface PersistFactory {

    Persist createPersist(Class<?> type);
}
