package com.lzh.game.start.model.wallet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lzh.game.start.model.currency.model.CurrencyType;
import com.lzh.game.repository.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 人物钱包
 */
@Data
@Document
public class Wallet extends BaseEntity<Long> {

    @Id
    private long playerId;
    /**
     * 当前身上的资金
     * k -> {@link CurrencyType}
     */
    private ConcurrentHashMap<CurrencyType, Long> money;
    /**
     * 历史获得的资金总和
     * 不会减少
     * k -> {@link CurrencyType}
     */
    private ConcurrentHashMap<CurrencyType, Long> history;

    public static Wallet of(Long playerId) {
        Wallet wallet = new Wallet();
        wallet.playerId = playerId;
        wallet.money = new ConcurrentHashMap<>(3);
        wallet.history = new ConcurrentHashMap<>(3);
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

    @Override
    public Long getKey() {
        return playerId;
    }
}
