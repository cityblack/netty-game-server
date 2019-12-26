package com.lzh.game.framework.cmd;

import com.lzh.game.socket.dispatcher.mapping.CmdMappingManage.CmdModel;

import java.util.List;

@FunctionalInterface
public interface ParseCmdLoad {

    List<CmdModel> parseAndLoad();
}
