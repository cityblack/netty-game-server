package com.lzh.game.framework.repository.persist;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Persist repository
 * Support mongo db
 * JPA?
 */
public interface PersistRepository {

    void save(PersistEntity<?> entity);

    void saveAll(Collection<PersistEntity<?>> entities);

    void update(PersistEntity<?> entity);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>>void update(PK pk, Class<T> clazz, Map<String, Object> change);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> void deleter(PK pk, Class<T> clazz);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> void deleter(T entity);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> T findById(PK pk, Class<T> clazz);

    <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> List<T> findAll(Class<T> clazz);
}
