package com.lzh.game.start.model.wallet.service;

import com.lzh.game.common.ApplicationUtils;
import com.lzh.game.common.event.EventBus;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.start.model.wallet.dao.WalletManage;
import com.lzh.game.start.log.LogFile;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.log.LoggerUtils;
import com.lzh.game.start.model.i18n.RequestException;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.wallet.Wallet;
import com.lzh.game.start.model.wallet.event.IncreaseMoneyEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WalletService {

    @Autowired
    private WalletManage walletManage;

    public long getCurrentCurrency(Player player, CurrencyType type) {
        return player.getWallet().getCurrentCurrency(type);
    }

    public long getHistoryCurrency(Player player, CurrencyType type) {
        return player.getWallet().getHistoryCurrency(type);
    }

    public void enoughCurrencyThrow(Player player, CurrencyType type, long needValue) {
        boolean flag = enoughCurrency(player, type, needValue);
        if (!flag) {
            throw new RequestException(type.notEnoughI18n());
        }
    }

    public boolean enoughCurrency(Player player, CurrencyType type, long needValue) {

        Wallet wallet = player.getWallet();
        long currentValue = wallet.getCurrentCurrency(type);
        if (currentValue <= 0) {
            return false;
        }
        return currentValue >= needValue;
    }

    public void reduceCurrency(Player player, CurrencyType type, long value, LogReason logReason) {
        Wallet wallet = player.getWallet();
        long currentValue = wallet.getCurrentCurrency(type);
        long diff = currentValue - value;
        if (diff < 0 && !type.allowNegative()) {
            throw new RequestException(type.notEnoughI18n());
        }
        wallet.setCurrentCurrency(type, diff);
        walletManage.update(wallet);

        logMoneyChange(player, type, currentValue, value, diff, logReason);
    }

    public void addCurrency(Player player, CurrencyType type, long value, LogReason logReason) {
        Wallet wallet = player.getWallet();
        long before = wallet.getCurrentCurrency(type);
        wallet.addCurrentCurrency(type, value);
        wallet.addHistoryValue(type, value);
        walletManage.update(wallet);

        long after = wallet.getCurrentCurrency(type);

        ApplicationUtils.getBean(EventBus.class)
                .post(IncreaseMoneyEvent.of(player, logReason, value, after, type));
        logMoneyChange(player, type, before, value, after, logReason);
    }

    public Wallet getWalletById(long player) {
        return walletManage.loadWallet(player);
    }

    private void logMoneyChange(Player player, CurrencyType type, long before, long value, long after, LogReason logReason) {
        LoggerUtils.of(LogFile.CURRENCY, logReason)
                .addParam("type", type.name())
                .addParam("value", value)
                .addParam("before", before)
                .addParam("after", after)
                .log(player);

    }
}
