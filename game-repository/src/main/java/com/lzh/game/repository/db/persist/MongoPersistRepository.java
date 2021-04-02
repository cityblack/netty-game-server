package com.lzh.game.repository.db.persist;

import com.lzh.game.repository.db.PersistEntity;
import com.lzh.game.repository.db.PersistRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MongoPersistRepository implements PersistRepository {

    private static final String ID_FIELD = "_id";

    private MongoOperations mongoOperations;

    private MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext;

    public MongoPersistRepository(MongoOperations mongoOperations, MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        this.mongoOperations = mongoOperations;
        this.mappingContext = mappingContext;
    }

    @Override
    public void save(PersistEntity entity) {
        mongoOperations.save(entity);
    }

    @Override
    public void saveAll(Collection<PersistEntity> entities) {
        entities.forEach(this::save);
    }

    @Override
    public void update(PersistEntity entity) {
        mongoOperations.save(entity);
    }

    @Override
    public <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> void update(PK pk, Class<T> clazz, Map<String, Object> change) {
        Query query = findKeyQuery(pk, clazz);
        Update update = new Update();
        change.forEach((k,v) -> update.set(k,v));
        mongoOperations.updateFirst(query, update, clazz);
    }

    @Override
    public <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> void deleter(PK pk, Class<T> clazz) {
        mongoOperations.remove(findKeyQuery(pk, clazz), clazz);
    }

    @Override
    public void deleter(PersistEntity entity) {

        mongoOperations.remove(entity);
    }

    @Override
    public <PK extends Serializable & Comparable<PK>, T extends PersistEntity<PK>> T findById(PK pk, Class<T> clazz) {
        return mongoOperations.findById(pk, clazz);
    }

    @Override
    public <T extends PersistEntity> List<T> findAll(Class<T> clazz) {
        return mongoOperations.findAll(clazz);
    }

    private <PK extends Serializable, T extends PersistEntity>Query findKeyQuery(PK pk, Class<T> clazz) {

        PersistentEntity persistentEntity = mappingContext.getPersistentEntity(clazz);
        String idKey = ID_FIELD;

        if (persistentEntity != null) {
            if (persistentEntity.getIdProperty() != null) {
                idKey = persistentEntity.getIdProperty().getName();
            }
        }
        return Query.query(Criteria.where(idKey).is(pk));
    }
}
