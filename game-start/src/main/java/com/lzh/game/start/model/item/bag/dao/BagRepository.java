package com.lzh.game.start.model.item.bag.dao;

import com.lzh.game.start.model.item.bag.Bag;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface BagRepository extends PagingAndSortingRepository<Bag, Long> {
}
