package com.lzh.game.start.model.wallet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.repository.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Player Wallet
 */
@Data
public class Wallet {

    /**
     * 当前身上的资金
     * k -> {@link CurrencyType}
     */
    private Map<CurrencyType, Long> money;
    /**
     * 历史获得的资金总和
     * 不会减少
     * k -> {@link CurrencyType}
     */
    private Map<CurrencyType, Long> history;

    public static Wallet of(Long playerId) {
        Wallet wallet = new Wallet();
        wallet.money = new HashMap<>(3);
        wallet.history = new HashMap<>(3);
        return wallet;
    }

    @JsonIgnore
    public long getCurrentCurrency(CurrencyType type) {
        return money.getOrDefault(type, 0L);
    }

    public long setCurrentCurrency(CurrencyType type, long value) {
        return money.put(type, value);
    }

    public void addCurrentCurrency(CurrencyType type, long value) {
        long total = money.getOrDefault(type, 0L) + value;
        money.put(type, total);
    }

    public void addHistoryValue(CurrencyType type, long value) {
        long total = history.getOrDefault(type, 0L) + value;
        history.put(type, total);
    }

    @JsonIgnore
    public long getHistoryCurrency(CurrencyType type) {
        return this.history.getOrDefault(type, 0L);
    }
}
