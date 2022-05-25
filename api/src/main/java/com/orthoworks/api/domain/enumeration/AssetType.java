package com.orthoworks.api.domain.enumeration;

/**
 * The AssetType enumeration.
 */
public enum AssetType {
    IMAGE("Image"),
    VIDEO("Video"),
    AUDIO("Audio");

    private final String value;

    AssetType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
