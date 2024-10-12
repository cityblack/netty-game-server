package com.lzh.game.start.model.common.service;

import com.lzh.game.framework.repository.DataRepository;
import com.lzh.game.framework.repository.anno.Repository;
import com.lzh.game.start.model.common.CommonIdGenerator;
import com.lzh.game.start.model.common.model.CommonData;
import com.lzh.game.start.server.AfterServerStartInit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonDataManage implements AfterServerStartInit {

    @Repository
    private DataRepository<Integer, CommonData> dataRepository;

    public Object getCommon(CommonIdGenerator id) {
        return getCommonData(id).getData();
    }

    public void updateCommonData(CommonIdGenerator id, Object data) {
        CommonData commonData = getCommonData(id);
        commonData.setData(data);
        dataRepository.save(commonData);
    }

    private CommonData getCommonData(CommonIdGenerator id) {
        return dataRepository.get(id.getId());
    }

    @Override
    public void init() {
        log.info("Global data loading.");
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
