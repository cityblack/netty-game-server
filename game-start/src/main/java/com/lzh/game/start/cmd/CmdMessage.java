package com.lzh.game.start.cmd;

import com.lzh.game.framework.cmd.DefaultCmdMappingManage;

/**
 * Start with the CM_ is mean request cmd. and start with SM_ is mean response cmd.
 * According to the rules, system will auto register cmd when {@link DefaultCmdMappingManage}
 * loading
 */
public interface CmdMessage {

    int CM_GM = -1;
    //========= Base -100 - -500 ==========
    int SM_NOTIFY = -100;

    // == login ==
    int CM_LOGIN = -102;
    int SM_LOGIN = -103;
    int CM_REGISTER = -104;
    int SM_REGISTER = -105;

    // == Item begin ====
    int CM_USE_ITEM = -170;

    ;

}
