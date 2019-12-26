package com.lzh.game.start.model.player.dao;

import com.lzh.game.start.model.player.model.PlayerEnt;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PlayerRepository extends PagingAndSortingRepository<PlayerEnt, Long> {

    boolean existsByAccount(String account);
}
