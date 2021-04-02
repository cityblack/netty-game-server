package com.lzh.game.start.model.target.handler.impl;

import com.lzh.game.common.event.SubscribeListener;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.Target;
import com.lzh.game.start.model.target.TargetType;
import com.lzh.game.start.model.target.TargetUtils;
import com.lzh.game.start.model.target.handler.AbstractTargetHandler;
import com.lzh.game.start.model.wallet.event.IncreaseMoneyEvent;
import com.lzh.game.start.model.wallet.service.WalletService;
import org.greenrobot.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoldTargetHandler extends AbstractTargetHandler implements SubscribeListener {

    @Autowired
    private WalletService walletService;

    @Override
    public void init(Player player, Target target) {
        if (target.isHistory()) {
            target.setCurrentValue(player.getWallet().getHistoryCurrency(CurrencyType.GOLD));
        } else {
            target.setCurrentValue(player.getWallet().getCurrentCurrency(CurrencyType.GOLD));
        }
    }

    @Override
    public TargetType type() {
        return TargetType.GOLD;
    }

    private void doComputed(Player player) {
        TargetUtils.foreachTargetModel(player, target -> getValue(target, player), type());
    }

    private long getValue(Target target, Player player) {
        return target.isHistory() ? walletService.getHistoryCurrency(player, CurrencyType.GOLD) : walletService.getCurrentCurrency(player, CurrencyType.GOLD);
    }

    @Subscribe
    public void onMoneyIncrease(IncreaseMoneyEvent event) {
        doComputed(event.getOwn());
    }
}
