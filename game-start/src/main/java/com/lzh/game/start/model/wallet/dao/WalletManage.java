package com.lzh.game.start.model.wallet.dao;

import com.lzh.game.start.model.wallet.Wallet;

public interface WalletManage {

    Wallet loadWallet(Long playerId);

    void update(Wallet wallet);

}
