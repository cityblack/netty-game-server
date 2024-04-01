package com.lzh.game.start.cmd.impl;


import com.lzh.game.start.cmd.DefaultCmdMappingManage;

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
    int CM_LOGOUT = -106;

    int CM_HELLO = -10086;
    int SM_HELLO = -10087;

}
