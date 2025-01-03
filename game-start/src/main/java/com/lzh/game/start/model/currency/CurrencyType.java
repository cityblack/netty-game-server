package com.lzh.game.start.model.currency;

import com.lzh.game.start.model.i18n.I18n;

public enum CurrencyType {
    // 金币
    GOLD() {
        @Override
        public boolean allowNegative() {
            return true;
        }

        @Override
        public int notEnoughI18n() {
            return I18n.NOT_ENOUGH_GOLD;
        }
    },
    //钻石
    DIAMOND {
        @Override
        public int notEnoughI18n() {
            return I18n.NOT_ENOUGH_DIAMOND;
        }
    },
    //积分
    INTEGRAL {
        @Override
        public int notEnoughI18n() {
            return I18n.NOT_ENOUGH_INTEGRAL;
        }
    },
    ;

    /**
     * 是否允许为负值
     * @return
     */
    public boolean allowNegative() {
        return false;
    }

    /**
     * 不足提示
     * @return
     */
    public abstract int notEnoughI18n();
}
