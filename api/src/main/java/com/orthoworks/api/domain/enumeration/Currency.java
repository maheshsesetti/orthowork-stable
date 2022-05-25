package com.orthoworks.api.domain.enumeration;

/**
 * The Currency enumeration.
 */
public enum Currency {
    INR("Rupee"),
    USD("US Dollar"),
    ETH("Ether");

    private final String value;

    Currency(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
