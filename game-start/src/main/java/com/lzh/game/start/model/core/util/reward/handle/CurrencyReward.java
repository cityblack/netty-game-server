package com.lzh.game.start.model.core.util.reward.handle;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.VerifyResult;
import com.lzh.game.start.model.core.util.reward.AbstractReward;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.SpringContext;

/**
 * 金钱
 * {type:"CURRENCY",value:"CurrencyType_value"}
 */
public class CurrencyReward extends AbstractReward<Player> {

    private CurrencyType type;

    private long value;

    @Override
    protected void doParse(String value) {
        String[] cont = value.split("_");
        this.type = CurrencyType.valueOf(cont[0]);
        this.value = Long.valueOf(cont[1]);
    }

    @Override
    public void doVerify(Player param, VerifyResult result, int multiple) {
        if (value * multiple <= 0) {
            result.fail();
            result.setErrorCode(I18n.SYS_ERROR);
        }
    }

    @Override
    public void reward(Player player, int multiple, LogReason logReason) {
        long computed = multiple * this.value;
        SpringContext.singleTon().getWalletService().addCurrency(player, type, computed, logReason);
    }
}
