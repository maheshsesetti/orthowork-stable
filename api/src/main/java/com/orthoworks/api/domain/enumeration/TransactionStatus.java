package com.orthoworks.api.domain.enumeration;

/**
 * The TransactionStatus enumeration.
 */
public enum TransactionStatus {
    DRAFT("Draft"),
    SUBMITTED("Submitted"),
    STARTED("Started"),
    IN_PROGRESS("In Progress"),
    SUCCESSFUL("Successful"),
    FAILED("Failed");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
