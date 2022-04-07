package com.lzh.game.framework.cmd;

import java.util.List;

@FunctionalInterface
public interface CmdParseFactory {

    List<CmdMappingManage.CmdModel> parseAndLoad();
}
