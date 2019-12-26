package com.lzh.game.start.cmd;


import com.lzh.game.framework.cmd.ParseCmdLoad;

import static com.lzh.game.socket.dispatcher.mapping.CmdMappingManage.CmdType;
import static com.lzh.game.socket.dispatcher.mapping.CmdMappingManage.CmdModel;

import org.checkerframework.checker.units.qual.C;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CmdParseHandle implements ParseCmdLoad {

    private CmdType parseCmdType(String name) {
        if (name.startsWith("CM_")) {
            return CmdType.REQUEST;
        } else if (name.startsWith("SM_")) {
            return CmdType.RESPONSE;
        }

        throw new IllegalArgumentException("Protocol's name dose't comply with the contract. name:" + name);
    }

    private int parseCmd(Field field, Class c) {
        return (Integer) ReflectionUtils.getField(field, c);
    }

    @Override
    public List<CmdModel> parseAndLoad() {
        Class c = CmdMessage.class;
        return Stream
                .of(c.getFields())
                .map(e -> CmdModel.of(parseCmd(e, c), parseCmdType(e.getName()), ""))
                .collect(Collectors.toList());

    }

}
