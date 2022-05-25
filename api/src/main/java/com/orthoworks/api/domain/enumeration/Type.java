package com.orthoworks.api.domain.enumeration;

/**
 * The Type enumeration.
 */
public enum Type {
    PHYGITAL("pNFT Phygital Art"),
    MINIATURE_COLLECTIBLE("Miniature Collectible ex: Ferrari"),
    LIMITED_EDITION("Limited Edition ex: Prosche xxx");

    private final String value;

    Type(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
