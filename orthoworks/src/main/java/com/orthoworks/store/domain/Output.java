package com.orthoworks.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Output.
 */
@Table("output")
public class Output implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Column("result")
    private String result;

    @Transient
    private Transaction transaction;

    @Column("transaction_id")
    private Long transactionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Output id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Instant getDate() {
        return this.date;
    }

    public Output date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getResult() {
        return this.result;
    }

    public Output result(String result) {
        this.setResult(result);
        return this;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Transaction getTransaction() {
        return this.transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
        this.transactionId = transaction != null ? transaction.getId() : null;
    }

    public Output transaction(Transaction transaction) {
        this.setTransaction(transaction);
        return this;
    }

    public Long getTransactionId() {
        return this.transactionId;
    }

    public void setTransactionId(Long transaction) {
        this.transactionId = transaction;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Output)) {
            return false;
        }
        return id != null && id.equals(((Output) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Output{" +
            "id=" + getId() +
            ", date='" + getDate() + "'" +
            ", result='" + getResult() + "'" +
            "}";
    }
}
