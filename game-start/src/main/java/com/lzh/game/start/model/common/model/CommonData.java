package com.lzh.game.start.model.common.model;

import com.lzh.game.repository.BaseEntity;
import com.lzh.game.repository.db.PersistEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document
@Data
public class CommonData extends BaseEntity<Integer> implements Serializable {

    @Id
    private Integer id;

    private Object data;

    @Override
    public Integer cacheKey() {
        return id;
    }

    @Override
    public Integer getKey() {
        return id;
    }
}
