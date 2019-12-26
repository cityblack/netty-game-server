package com.lzh.game.start.model.common.service;

import com.lzh.game.repository.CacheDataRepository;
import com.lzh.game.start.model.common.CommonIdGenerator;
import com.lzh.game.start.model.common.model.CommonData;
import com.lzh.game.start.model.common.repository.CommonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@Slf4j
public class CommonDataManage {

    @Autowired
    private CacheDataRepository dataRepository;

    @Autowired
    private CommonRepository repository;

    public Object getCommon(CommonIdGenerator id) {
        return getCommonData(id).getData();
    }

    public void updateCommonData(CommonIdGenerator id, Object data) {
        CommonData commonData = getCommonData(id);
        commonData.setData(data);
        dataRepository.update(commonData);
    }

    private CommonData getCommonData(CommonIdGenerator id) {
        return dataRepository.get(id.getId(), CommonData.class);
    }

    public void init() {
        log.info("Global data loading.");

        Stream.of(CommonIdGenerator.values()).forEach(idGenerator -> {

            dataRepository.enhanceLoadOrCreate(idGenerator.getId(), CommonData.class
                    , repository, (e) -> {
                        CommonData dataEnt = new CommonData();
                        dataEnt.setId(e);
                        dataEnt.setData(idGenerator.of());
                        return dataEnt;
                    });
        });
    }
}
