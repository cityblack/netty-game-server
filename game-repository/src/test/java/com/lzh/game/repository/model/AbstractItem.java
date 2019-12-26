package com.lzh.game.repository.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
public class AbstractItem {

    @Id
    private String id;

    private String name;

    private int num;

}
