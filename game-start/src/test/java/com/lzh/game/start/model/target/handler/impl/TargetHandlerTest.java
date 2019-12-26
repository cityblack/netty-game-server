package com.lzh.game.start.model.target.handler.impl;

import com.lzh.game.start.PlayerUtils;
import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.model.target.TargetDef;
import com.lzh.game.start.model.target.TargetType;
import com.lzh.game.start.model.target.TargetUtils;
import com.lzh.game.start.model.target.model.TargetModelSign;
import com.lzh.game.start.model.wallet.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {com.lzh.game.start.App.class})
@Slf4j
public class TargetHandlerTest {
    @Autowired
    private WalletService walletService;

    @Test
    public void onMoneyIncrease() {
        Player player = PlayerUtils.get();
        TargetUtils.produceTarget(player, 1, new TargetDef[]{buildDef()}, TargetModelSign.ACHIEVEMENT);
        walletService.addCurrency(player, CurrencyType.GOLD, 100, LogReason.CONSOLE);
    }

    private TargetDef buildDef() {
        TargetDef def = new TargetDef();
        def.setValue(500);
        def.setType(TargetType.GOLD);
        def.setKey(1);
        return def;
    }
}