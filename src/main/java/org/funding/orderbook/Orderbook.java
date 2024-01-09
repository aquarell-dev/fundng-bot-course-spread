package org.funding.orderbook;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public abstract class Orderbook {
    public final String coin;
    protected final TreeMap<Double, Double> bids = new TreeMap<>();
    protected final TreeMap<Double, Double> asks = new TreeMap<>();

    public Orderbook(String coin) {
        this.coin = coin;
    }

    public abstract void updateBids(JSONArray bidsArray, Boolean snapshot);

    public abstract void updateAsks(JSONArray asksArray, Boolean snapshot);

    public synchronized Double getBestAsk() {
        return asks.firstKey();
    }

    public synchronized Double getBestBid() {
        return bids.lastKey();
    }

    public synchronized TreeMap<Double, Double> getBids() {
        return bids;
    }

    public synchronized TreeMap<Double, Double> getAsks() {
        return asks;
    }

    public synchronized Double calculateBuyAveragePrice(int volumeInUSDT) throws NotEnoughVolume {
        return calculateAveragePrice(asks, volumeInUSDT);
    }

    public synchronized Double calculateSellAveragePrice(int volumeInUSDT) throws NotEnoughVolume {
        return calculateAveragePrice(bids.descendingMap(), volumeInUSDT);
    }

    private synchronized Double calculateAveragePrice(NavigableMap<Double, Double> orderBook, int usdtAmount) throws NotEnoughVolume {
        double remainingAmountUSDT = usdtAmount;
        double totalPrice = 0;
        double totalVolumeInCoins = 0;

        for (Map.Entry<Double, Double> entry : orderBook.entrySet()) {
            double price = entry.getKey();
            double volume = entry.getValue();
            double volumeAtThisPriceInUSDT = price * volume;

            if (remainingAmountUSDT <= 0) {
                break;
            }

            if (remainingAmountUSDT < volumeAtThisPriceInUSDT) {
                double volumeInCoinsToBeBought = volume - (volumeAtThisPriceInUSDT - remainingAmountUSDT) / price;
                totalPrice += price * volumeInCoinsToBeBought;
                totalVolumeInCoins += volumeInCoinsToBeBought;
                remainingAmountUSDT = 0;
                break; // We've bought all the coins needed
            }

            totalPrice += volumeAtThisPriceInUSDT;
            totalVolumeInCoins += volume;
            remainingAmountUSDT -= volumeAtThisPriceInUSDT;
        }

        // if there's not enough volume in the orderbook to fill my order
        if (remainingAmountUSDT > 0) {
            throw new NotEnoughVolume("There ain't enough volume in the orderbook");
        }

        return totalPrice / totalVolumeInCoins; // Calculate the average price
    }
}
