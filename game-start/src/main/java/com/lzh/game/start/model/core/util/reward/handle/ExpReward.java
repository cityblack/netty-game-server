package com.lzh.game.start.model.core.util.reward.handle;

import com.lzh.game.start.log.LogReason;
import com.lzh.game.start.model.core.util.VerifyResult;
import com.lzh.game.start.model.core.util.reward.AbstractReward;
import com.lzh.game.start.model.i18n.I18n;
import com.lzh.game.start.model.player.Player;
import com.lzh.game.start.util.SpringContext;

public class ExpReward extends AbstractReward<Player> {

    private long exp;

    @Override
    protected void doParse(String value) {
        this.exp = Long.valueOf(value);
    }

    @Override
    public void doVerify(Player param, VerifyResult result, int multiple) {
        if ((exp * multiple) == 0) {
            result.fail();
            result.setErrorCode(I18n.SYS_ERROR);
        }
    }

    @Override
    public void reward(Player player, int multiple, LogReason logReason) {
        long sumExp = exp * multiple;
        SpringContext.singleTon().getPlayerExtService().addExp(player, sumExp, logReason);
    }
}
