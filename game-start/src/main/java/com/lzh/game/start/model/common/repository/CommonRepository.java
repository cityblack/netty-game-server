package com.lzh.game.start.model.common.repository;

import com.lzh.game.start.model.common.model.CommonData;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CommonRepository extends PagingAndSortingRepository<CommonData, Integer> {
}
