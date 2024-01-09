package org.funding.orderbook;

public class NotEnoughVolume extends RuntimeException {
    public NotEnoughVolume(String message) {
        super(message);
    }
}
