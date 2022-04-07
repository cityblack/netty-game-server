package com.lzh.game.start.cmd;


import com.lzh.game.framework.cmd.CmdMappingManage.CmdModel;
import com.lzh.game.framework.cmd.CmdMappingManage.CmdType;
import com.lzh.game.framework.cmd.CmdParseFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class CmdParseHandle implements CmdParseFactory {

    private CmdType parseCmdType(String name) {
        if (name.startsWith("CM_")) {
            return CmdType.REQUEST;
        } else if (name.startsWith("SM_")) {
            return CmdType.RESPONSE;
        }

        throw new IllegalArgumentException("Protocol's name dosen't comply with the contract. name:" + name);
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
