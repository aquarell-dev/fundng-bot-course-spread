package org.funding.exchange;

import java.net.URI;

public enum SpotExchange {
    MEXC ("Mexc Global", "", 0),
    KUCOIN ("KuCoin", "", 0);

    public final String exchangeTitle;
    public final URI connectionUri;
    public final double takerFee;

    private SpotExchange(String exchangeTitle, String connectionUri, double takerFee) {
        this.exchangeTitle = exchangeTitle;
        this.connectionUri = URI.create(connectionUri);
        this.takerFee = takerFee;
    }
}
