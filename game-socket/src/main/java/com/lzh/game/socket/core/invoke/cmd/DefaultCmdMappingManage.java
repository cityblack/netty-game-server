package com.lzh.game.socket.core.invoke.cmd;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCmdMappingManage implements CmdMappingManage, ApplicationContextAware {

    private Map<Integer, CmdModel> cmdMap = new ConcurrentHashMap<>();

    private CmdParseFactory cmdParseFactory;

    @Override
    public boolean contain(int cmd) {
        return cmdMap.containsKey(cmd);
    }

    @Override
    public Collection<CmdModel> getAllCmd() {
        return cmdMap.values();
    }

    @Override
    public CmdModel getCmd(int cmd) {
        return cmdMap.get(cmd);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        loadCmd();
    }

    private void loadCmd() {
        // CmdModel cmdModel = CmdModel.of(cmd, cmdType, desc, proto);
        List<CmdModel> models = cmdParseFactory.parseAndLoad();
        for (CmdModel model: models) {
            register(model);
        }
    }

    @Override
    public void register(CmdModel model) throws IllegalArgumentException {
        int cmd = model.getCmd();

        if (cmd == 0) {
            throw new IllegalArgumentException("Cmd or mapping class must not null.");
        }
        if (contain(cmd)) {
            throw new IllegalArgumentException("Cmd must unique. Had the same " + cmd + " in repository.");
        }

        cmdMap.put(cmd, model);
    }

    public DefaultCmdMappingManage(CmdParseFactory cmdParseFactory) {
        this.cmdParseFactory = cmdParseFactory;
    }
}
