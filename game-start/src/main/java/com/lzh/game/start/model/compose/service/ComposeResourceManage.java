package com.lzh.game.start.model.compose.service;

import com.lzh.game.start.model.compose.resource.ComposeResource;
import com.lzh.game.resource.Static;
import com.lzh.game.resource.Storage;
import org.springframework.stereotype.Component;

@Component
public class ComposeResourceManage {

    @Static
    private Storage<Integer, ComposeResource> composeResourceStorage;

    public ComposeResource findComposeResourceById(int id) {
        return composeResourceStorage.getOrThrow(id);
    }
}
