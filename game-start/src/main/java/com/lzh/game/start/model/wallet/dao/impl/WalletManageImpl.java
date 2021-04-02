package com.lzh.game.start.model.wallet.dao.impl;

import com.lzh.game.repository.DataRepository;
import com.lzh.game.repository.Repository;
import com.lzh.game.start.model.wallet.Wallet;
import com.lzh.game.start.model.wallet.dao.WalletManage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletManageImpl implements WalletManage {

    @Repository
    private DataRepository<Long, Wallet> dataRepository;

    @Override
    public Wallet loadWallet(Long playerId) {
        return dataRepository.loadOrCreate(playerId, Wallet::of);
    }

    @Override
    public void update(Wallet wallet) {
        dataRepository.update(wallet);
    }


}
