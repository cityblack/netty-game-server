package com.lzh.game.socket.core.invoke.cmd;

import java.util.List;

@FunctionalInterface
public interface CmdParseFactory {

    List<CmdMappingManage.CmdModel> parseAndLoad();
}
