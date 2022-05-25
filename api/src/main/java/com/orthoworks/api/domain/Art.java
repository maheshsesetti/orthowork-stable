package com.orthoworks.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orthoworks.api.domain.enumeration.AssetType;
import com.orthoworks.api.domain.enumeration.Type;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Art.
 */
@Entity
@Table(name = "art")
public class Art implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Size(min = 3)
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(min = 2)
    @Column(name = "handle", nullable = false)
    private String handle;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "asset_type", nullable = false)
    private AssetType assetType;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private Type type;

    @ManyToMany
    @JoinTable(
        name = "rel_art__collection",
        joinColumns = @JoinColumn(name = "art_id"),
        inverseJoinColumns = @JoinColumn(name = "collection_id")
    )
    @JsonIgnoreProperties(value = { "features", "arts" }, allowSetters = true)
    private Set<Collection> collections = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Art id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Art name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHandle() {
        return this.handle;
    }

    public Art handle(String handle) {
        this.setHandle(handle);
        return this;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public AssetType getAssetType() {
        return this.assetType;
    }

    public Art assetType(AssetType assetType) {
        this.setAssetType(assetType);
        return this;
    }

    public void setAssetType(AssetType assetType) {
        this.assetType = assetType;
    }

    public Type getType() {
        return this.type;
    }

    public Art type(Type type) {
        this.setType(type);
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<Collection> getCollections() {
        return this.collections;
    }

    public void setCollections(Set<Collection> collections) {
        this.collections = collections;
    }

    public Art collections(Set<Collection> collections) {
        this.setCollections(collections);
        return this;
    }

    public Art addCollection(Collection collection) {
        this.collections.add(collection);
        collection.getArts().add(this);
        return this;
    }

    public Art removeCollection(Collection collection) {
        this.collections.remove(collection);
        collection.getArts().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Art)) {
            return false;
        }
        return id != null && id.equals(((Art) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Art{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", handle='" + getHandle() + "'" +
            ", assetType='" + getAssetType() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
