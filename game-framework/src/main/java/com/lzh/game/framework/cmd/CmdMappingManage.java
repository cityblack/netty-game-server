package com.lzh.game.framework.cmd;

import lombok.Data;

import java.util.Collection;

public interface CmdMappingManage {

    boolean contain(int cmd);

    Collection<CmdModel> getAllCmd();

    CmdModel getCmd(int cmd);

    void register(CmdModel cmdModel);

    enum CmdType {
        REQUEST,
        RESPONSE
    }

    @Data
    class CmdModel {
        int cmd;
        CmdType cmdType;
        String desc;

        public static CmdModel of(int cmd, CmdType cmdType, String desc) {
            CmdModel cmdModel = new CmdModel();
            cmdModel.cmd = cmd;
            cmdModel.cmdType = cmdType;
            cmdModel.desc = desc;
            return cmdModel;
        }
    }
}
