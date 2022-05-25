package com.orthoworks.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A BrandCategory.
 */
@Entity
@Table(name = "brand_category")
public class BrandCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "date_added")
    private LocalDate dateAdded;

    @Column(name = "date_modified")
    private LocalDate dateModified;

    @ManyToOne
    @JsonIgnoreProperties(value = { "parent", "brands" }, allowSetters = true)
    private BrandCategory parent;

    @ManyToMany
    @JoinTable(
        name = "rel_brand_category__brand",
        joinColumns = @JoinColumn(name = "brand_category_id"),
        inverseJoinColumns = @JoinColumn(name = "brand_id")
    )
    @JsonIgnoreProperties(value = { "categories" }, allowSetters = true)
    private Set<Brand> brands = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public BrandCategory id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return this.description;
    }

    public BrandCategory description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return this.sortOrder;
    }

    public BrandCategory sortOrder(Integer sortOrder) {
        this.setSortOrder(sortOrder);
        return this;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public BrandCategory dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public BrandCategory dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public BrandCategory getParent() {
        return this.parent;
    }

    public void setParent(BrandCategory brandCategory) {
        this.parent = brandCategory;
    }

    public BrandCategory parent(BrandCategory brandCategory) {
        this.setParent(brandCategory);
        return this;
    }

    public Set<Brand> getBrands() {
        return this.brands;
    }

    public void setBrands(Set<Brand> brands) {
        this.brands = brands;
    }

    public BrandCategory brands(Set<Brand> brands) {
        this.setBrands(brands);
        return this;
    }

    public BrandCategory addBrand(Brand brand) {
        this.brands.add(brand);
        brand.getCategories().add(this);
        return this;
    }

    public BrandCategory removeBrand(Brand brand) {
        this.brands.remove(brand);
        brand.getCategories().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof BrandCategory)) {
            return false;
        }
        return id != null && id.equals(((BrandCategory) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "BrandCategory{" +
            "id=" + getId() +
            ", description='" + getDescription() + "'" +
            ", sortOrder=" + getSortOrder() +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
