package com.lzh.game.repository.entity;

import com.lzh.game.repository.BaseEntity;
import com.lzh.game.repository.db.PersistEntity;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@ToString
@Document
public class Common extends BaseEntity<String> implements Serializable {

    @Id
    private String id;

    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getKey() {
        return id;
    }
}
