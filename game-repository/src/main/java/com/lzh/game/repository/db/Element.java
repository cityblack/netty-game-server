package com.lzh.game.repository.db;

import lombok.ToString;

import java.io.Serializable;

@ToString
public class Element implements Serializable {

    private static final long serialVersionUID = -1320825769465308983L;

    private Serializable id;

    private Class<? extends PersistEntity> clazz;

    private PersistEntity entity;

    private EventType eventType;

    private Element(Serializable id, EventType eventType, Class<? extends PersistEntity> clazz) {
        this.id = id;
        this.eventType = eventType;
        this.clazz = clazz;
    }

    protected Element(Serializable id, Class<? extends PersistEntity> clazz, PersistEntity entity, EventType eventType) {
        this.id = id;
        this.clazz = clazz;
        this.entity = entity;
        this.eventType = eventType;
    }

    public static Element saveOf(PersistEntity entity, Class<? extends PersistEntity> clazz) {
        return new Element(entity.getKey(), clazz, entity, EventType.SAVE);
    }

    public static Element deleterOf(PersistEntity entity, Class<? extends PersistEntity> clazz) {
        return new Element(entity.getKey(), clazz, entity, EventType.DELETER);
    }

    public static Element updateOf(PersistEntity entity, Class<? extends PersistEntity> clazz) {
        return new Element(entity.getKey(), clazz, entity, EventType.UPDATE);
    }

    public Serializable getId() {
        return id;
    }

    public void setId(Serializable id) {
        this.id = id;
    }

    public Class<? extends PersistEntity> getClazz() {
        return clazz;
    }

    public void setClazz(Class<? extends PersistEntity> clazz) {
        this.clazz = clazz;
    }

    public PersistEntity getEntity() {
        return entity;
    }

    public void setEntity(PersistEntity entity) {
        this.entity = entity;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public interface EventTypeBack {

        void onSave(Element element);

        void onUpdate(Element element);

        void onDeleter(Element element);
    }

    public void eventBack(EventTypeBack back) {
        switch (this.getEventType()) {
            case SAVE: back.onSave(this); break;
            case UPDATE: back.onUpdate(this); break;
            case DELETER: back.onDeleter(this); break;
            default: break;
        }
    }
}
