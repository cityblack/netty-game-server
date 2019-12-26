package com.lzh.game.start.model.wallet.dao;

import com.lzh.game.start.model.wallet.Wallet;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface WalletRepository extends PagingAndSortingRepository<Wallet, Long> {
}
