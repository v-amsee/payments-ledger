package com.vamsee.ledger.domain;

import java.util.Objects;

public record Money(long pence, String currency) {
    public Money {
        Objects.requireNonNull(currency, "currency");
        if (currency.length() != 3) throw new IllegalArgumentException("ISO code");
    }
    public Money plus(Money o) {
        if (!currency.equals(o.currency)) throw new IllegalArgumentException("currency mismatch");
        return new Money(Math.addExact(pence, o.pence), currency);
    }
}