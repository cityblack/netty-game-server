package com.lzh.game.framework.client.model.hello;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseHello {

    private String content;

    private int version;
}
