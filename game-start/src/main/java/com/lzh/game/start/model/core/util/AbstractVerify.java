package com.lzh.game.start.model.core.util;

import com.lzh.game.start.model.i18n.RequestException;

public abstract class AbstractVerify implements Verify {

    @Override
    public void verifyThrow(Object param) throws RequestException {
        verifyThrow(param, 1);
    }

    @Override
    public boolean verify(Object param, int multiple) {
        return doVerify(param, multiple).isSuccess();
    }

    @Override
    public void verifyThrow(Object param, int multiple) throws RequestException {
        VerifyResult result = doVerify(param, multiple);
        if (!result.isSuccess()) {
            throw new RequestException(result.getErrorCode());
        }
    }

    private VerifyResult doVerify(Object param, int multiple) {
        VerifyResult result = new VerifyResult();
        doVerify(param, result, multiple);
        return result;
    }

    public abstract void doVerify(Object param, VerifyResult result, int multiple);

}
