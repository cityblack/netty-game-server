package com.lzh.game.resource.inject;

import com.lzh.game.resource.Id;
import com.lzh.game.resource.Resource;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@Resource
public class ConfigValueResource {

    @Id
    private String key;

    private String value;

}
