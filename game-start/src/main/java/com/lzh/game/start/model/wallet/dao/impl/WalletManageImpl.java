package com.lzh.game.start.model.wallet.dao.impl;

import com.lzh.game.repository.CacheDataRepository;
import com.lzh.game.start.model.wallet.Wallet;
import com.lzh.game.start.model.wallet.dao.WalletManage;
import com.lzh.game.start.model.wallet.dao.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletManageImpl implements WalletManage {

    @Autowired
    private CacheDataRepository cacheDataRepository;

    @Autowired
    private WalletRepository repository;

    @Override
    public Wallet loadWallet(Long playerId) {
        return cacheDataRepository.enhanceLoadOrCreate(playerId, Wallet.class
                , repository, Wallet::of);
    }

    @Override
    public void update(Wallet wallet) {
        cacheDataRepository.update(wallet);
    }


}
