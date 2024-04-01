package com.lzh.game.start.cmd;

import java.util.List;

@FunctionalInterface
public interface CmdParseFactory {

    List<CmdMappingManage.CmdModel> parseAndLoad();
}
