package com.lzh.game.framework.cmd;

import com.lzh.game.socket.dispatcher.mapping.CmdMappingManage;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultCmdMappingManage implements CmdMappingManage, ApplicationContextAware {

    private Map<Integer, CmdModel> cmdMap = new ConcurrentHashMap<>();

    private ParseCmdLoad parseCmdLoad;

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
        List<CmdModel> models = parseCmdLoad.parseAndLoad();
        for (CmdModel model: models) {
            register(model);
        }
    }

    protected CmdMappingManage register(CmdModel model) throws IllegalArgumentException {
        int cmd = model.getCmd();

        if (cmd == 0) {
            throw new IllegalArgumentException("Cmd or mapping class must not null.");
        }
        if (contain(cmd)) {
            throw new IllegalArgumentException("Cmd must unique. Had the same " + cmd + " in repository.");
        }

        cmdMap.put(cmd, model);
        return this;
    }

    public DefaultCmdMappingManage(ParseCmdLoad parseCmdLoad) {
        this.parseCmdLoad = parseCmdLoad;
    }
}
