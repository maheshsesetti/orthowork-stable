package com.orthoworks.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orthoworks.store.domain.enumeration.AuctionType;
import com.orthoworks.store.domain.enumeration.CollectionType;
import com.orthoworks.store.domain.enumeration.Currency;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Collection.
 */
@Table("collection")
public class Collection implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Min(value = 1)
    @Max(value = 10000)
    @Column("count")
    private Integer count;

    @NotNull(message = "must not be null")
    @Column("collection_type")
    private CollectionType collectionType;

    @NotNull(message = "must not be null")
    @Column("auction_type")
    private AuctionType auctionType;

    @NotNull(message = "must not be null")
    @Column("min_range")
    private Float minRange;

    @NotNull(message = "must not be null")
    @Column("max_range")
    private Float maxRange;

    @NotNull(message = "must not be null")
    @Column("currency")
    private Currency currency;

    @NotNull(message = "must not be null")
    @Column("owner")
    private String owner;

    @Transient
    @JsonIgnoreProperties(value = { "collection" }, allowSetters = true)
    private Set<Feature> features = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "collections" }, allowSetters = true)
    private Set<Art> arts = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Collection id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Collection name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return this.title;
    }

    public Collection title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getCount() {
        return this.count;
    }

    public Collection count(Integer count) {
        this.setCount(count);
        return this;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public CollectionType getCollectionType() {
        return this.collectionType;
    }

    public Collection collectionType(CollectionType collectionType) {
        this.setCollectionType(collectionType);
        return this;
    }

    public void setCollectionType(CollectionType collectionType) {
        this.collectionType = collectionType;
    }

    public AuctionType getAuctionType() {
        return this.auctionType;
    }

    public Collection auctionType(AuctionType auctionType) {
        this.setAuctionType(auctionType);
        return this;
    }

    public void setAuctionType(AuctionType auctionType) {
        this.auctionType = auctionType;
    }

    public Float getMinRange() {
        return this.minRange;
    }

    public Collection minRange(Float minRange) {
        this.setMinRange(minRange);
        return this;
    }

    public void setMinRange(Float minRange) {
        this.minRange = minRange;
    }

    public Float getMaxRange() {
        return this.maxRange;
    }

    public Collection maxRange(Float maxRange) {
        this.setMaxRange(maxRange);
        return this;
    }

    public void setMaxRange(Float maxRange) {
        this.maxRange = maxRange;
    }

    public Currency getCurrency() {
        return this.currency;
    }

    public Collection currency(Currency currency) {
        this.setCurrency(currency);
        return this;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getOwner() {
        return this.owner;
    }

    public Collection owner(String owner) {
        this.setOwner(owner);
        return this;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Set<Feature> getFeatures() {
        return this.features;
    }

    public void setFeatures(Set<Feature> features) {
        if (this.features != null) {
            this.features.forEach(i -> i.setCollection(null));
        }
        if (features != null) {
            features.forEach(i -> i.setCollection(this));
        }
        this.features = features;
    }

    public Collection features(Set<Feature> features) {
        this.setFeatures(features);
        return this;
    }

    public Collection addFeatures(Feature feature) {
        this.features.add(feature);
        feature.setCollection(this);
        return this;
    }

    public Collection removeFeatures(Feature feature) {
        this.features.remove(feature);
        feature.setCollection(null);
        return this;
    }

    public Set<Art> getArts() {
        return this.arts;
    }

    public void setArts(Set<Art> arts) {
        if (this.arts != null) {
            this.arts.forEach(i -> i.removeCollection(this));
        }
        if (arts != null) {
            arts.forEach(i -> i.addCollection(this));
        }
        this.arts = arts;
    }

    public Collection arts(Set<Art> arts) {
        this.setArts(arts);
        return this;
    }

    public Collection addArt(Art art) {
        this.arts.add(art);
        art.getCollections().add(this);
        return this;
    }

    public Collection removeArt(Art art) {
        this.arts.remove(art);
        art.getCollections().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Collection)) {
            return false;
        }
        return id != null && id.equals(((Collection) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Collection{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", title='" + getTitle() + "'" +
            ", count=" + getCount() +
            ", collectionType='" + getCollectionType() + "'" +
            ", auctionType='" + getAuctionType() + "'" +
            ", minRange=" + getMinRange() +
            ", maxRange=" + getMaxRange() +
            ", currency='" + getCurrency() + "'" +
            ", owner='" + getOwner() + "'" +
            "}";
    }
}
