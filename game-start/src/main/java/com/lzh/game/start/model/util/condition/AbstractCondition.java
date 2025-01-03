package com.lzh.game.start.model.util.condition;

import com.lzh.game.start.model.util.VerifyResult;
import com.lzh.game.start.model.i18n.RequestException;

public abstract class AbstractCondition implements Condition {

    public final AbstractCondition parse(String value) {
        doParse(value);
        return this;
    }

    @Override
    public boolean verify(Object param) {
        return doVerify(param).isSuccess();
    }

    @Override
    public void verifyThrow(Object param) throws RequestException {
        VerifyResult result = doVerify(param);
        if (!result.isSuccess()) {
            throw new RequestException(result.getErrorCode());
        }
    }

    public abstract void doVerify(Object param,  VerifyResult result);

    public abstract void doParse(String value);

    private VerifyResult doVerify(Object param) {
        VerifyResult result = VerifyResult.of();
        doVerify(param, result);
        return result;
    }
}
