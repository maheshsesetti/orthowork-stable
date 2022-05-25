package com.orthoworks.api.domain.enumeration;

/**
 * The AuctionType enumeration.
 */
public enum AuctionType {
    FLAT("Flat rate"),
    ENGLISH("English Auction");

    private final String value;

    AuctionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
