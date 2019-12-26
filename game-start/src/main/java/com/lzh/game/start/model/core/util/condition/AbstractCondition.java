package com.lzh.game.start.model.core.util.condition;

import com.lzh.game.start.model.core.util.VerifyResult;
import com.lzh.game.start.model.i18n.RequestException;

public abstract class AbstractCondition<T> implements Condition<T> {

    public final AbstractCondition parse(String value) {
        doParse(value);
        return this;
    }

    @Override
    public boolean verify(T param) {
        return doVerify(param).isSuccess();
    }

    @Override
    public void verifyThrow(T param) throws RequestException {
        VerifyResult result = doVerify(param);
        if (!result.isSuccess()) {
            throw new RequestException(result.getErrorCode());
        }
    }

    /* @Override
    public boolean verify(Player player, ConditionDynamicArgs args) {

        return doVerify(player, args).isSuccess();
    }

    @Override
    public void verifyThrow(Player player, ConditionDynamicArgs args) throws RequestException {
        VerifyResult result = doVerify(player, args);

        if (!result.isSuccess()) {
            throw new RequestException(result.getErrorCode());
        }
    }*/

    public abstract void doVerify(T param,  VerifyResult result);

    public abstract void doParse(String value);

    private VerifyResult doVerify(T param) {
        VerifyResult result = VerifyResult.of();
        doVerify(param, result);
        return result;
    }
}
