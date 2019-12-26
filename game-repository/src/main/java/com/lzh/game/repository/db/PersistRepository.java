package com.lzh.game.repository.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface PersistRepository {

    void save(PersistEntity entity);

    void saveAll(Collection<PersistEntity> entities);

    void update(PersistEntity entity);

    <PK extends Serializable, T extends PersistEntity>void update(PK pk, Class<T> clazz, Map<String, Object> change);

    <PK extends Serializable, T extends PersistEntity> void deleter(PK pk, Class<T> clazz);

    void deleter(PersistEntity entity);
}
