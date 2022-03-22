package com.lzh.game.repository.db;

import java.io.Serializable;

/**
 * DB Object
 * The object of persisting to db must be implements the interface
 * @param <PK>
 */
public interface PersistEntity<PK extends Serializable & Comparable<PK>> {


    PK getKey();
    /**
     * Serialize function. When the return value is {@code true}, will not persist the entity
     * @return
     */
    default boolean serialize() { return true; }

    /**
     * When data loaded from db. Please call the function
     */
    default void deserialize() {}
}
