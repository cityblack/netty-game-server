package com.lzh.game.repository.repository;

import com.lzh.game.repository.entity.PlayerItemBox;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ItemBoxRepository extends PagingAndSortingRepository<PlayerItemBox, String> {

}
