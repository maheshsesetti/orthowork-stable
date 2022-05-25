package com.orthoworks.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orthoworks.store.domain.enumeration.TransactionStatus;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Transaction.
 */
@Table("transaction")
public class Transaction implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Column("status")
    private TransactionStatus status;

    @NotNull(message = "must not be null")
    @Column("date")
    private Instant date;

    @Transient
    @JsonIgnoreProperties(value = { "transaction" }, allowSetters = true)
    private Set<Data> data = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "features", "arts" }, allowSetters = true)
    private Collection collection;

    @Transient
    private Output result;

    @Column("collection_id")
    private Long collectionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Transaction id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Transaction title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TransactionStatus getStatus() {
        return this.status;
    }

    public Transaction status(TransactionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public Instant getDate() {
        return this.date;
    }

    public Transaction date(Instant date) {
        this.setDate(date);
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Set<Data> getData() {
        return this.data;
    }

    public void setData(Set<Data> data) {
        if (this.data != null) {
            this.data.forEach(i -> i.setTransaction(null));
        }
        if (data != null) {
            data.forEach(i -> i.setTransaction(this));
        }
        this.data = data;
    }

    public Transaction data(Set<Data> data) {
        this.setData(data);
        return this;
    }

    public Transaction addData(Data data) {
        this.data.add(data);
        data.setTransaction(this);
        return this;
    }

    public Transaction removeData(Data data) {
        this.data.remove(data);
        data.setTransaction(null);
        return this;
    }

    public Collection getCollection() {
        return this.collection;
    }

    public void setCollection(Collection collection) {
        this.collection = collection;
        this.collectionId = collection != null ? collection.getId() : null;
    }

    public Transaction collection(Collection collection) {
        this.setCollection(collection);
        return this;
    }

    public Output getResult() {
        return this.result;
    }

    public void setResult(Output output) {
        if (this.result != null) {
            this.result.setTransaction(null);
        }
        if (output != null) {
            output.setTransaction(this);
        }
        this.result = output;
    }

    public Transaction result(Output output) {
        this.setResult(output);
        return this;
    }

    public Long getCollectionId() {
        return this.collectionId;
    }

    public void setCollectionId(Long collection) {
        this.collectionId = collection;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        return id != null && id.equals(((Transaction) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Transaction{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", status='" + getStatus() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
