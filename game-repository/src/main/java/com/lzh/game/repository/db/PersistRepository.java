package com.lzh.game.repository.db;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PersistRepository {

    void save(PersistEntity entity);

    void saveAll(Collection<PersistEntity> entities);

    void update(PersistEntity entity);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>>void update(PK pk, Class<T> clazz, Map<String, Object> change);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> void deleter(PK pk, Class<T> clazz);

    void deleter(PersistEntity entity);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> T findById(PK pk, Class<T> clazz);

    <T extends PersistEntity>List<T> findAll(Class<T> clazz);
}
