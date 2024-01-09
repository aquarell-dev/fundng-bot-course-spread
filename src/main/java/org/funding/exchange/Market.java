package org.funding.exchange;

public enum Market {
    FUTURES ("Futures"),
    SPOT ("Spot");

    public final String market;

    private Market(String market) {
        this.market = market;
    }
}
