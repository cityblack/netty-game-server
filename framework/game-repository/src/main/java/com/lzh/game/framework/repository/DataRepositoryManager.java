package com.lzh.game.framework.repository;

/**
 * @author zehong.l
 * @since 2024-10-12 18:50
 **/
public interface DataRepositoryManager {

    DataRepository<?,?> findRepository(Class<?> type);
}
