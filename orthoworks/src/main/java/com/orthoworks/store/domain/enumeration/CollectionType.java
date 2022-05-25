package com.orthoworks.store.domain.enumeration;

/**
 * The CollectionType enumeration.
 */
public enum CollectionType {
    IMAGE("Image"),
    AUDIO("Audio"),
    IMAGEAUDIO("Image and Audio"),
    VIDEO("Video");

    private final String value;

    CollectionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
