package com.orthoworks.store.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orthoworks.store.domain.enumeration.BrandStatus;
import com.orthoworks.store.domain.enumeration.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Brand.
 */
@Table("brand")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("title")
    private String title;

    @Column("keywords")
    private String keywords;

    @Column("description")
    private String description;

    @Column("image")
    private byte[] image;

    @Column("image_content_type")
    private String imageContentType;

    @Column("rating")
    private Integer rating;

    @Column("status")
    private BrandStatus status;

    @NotNull(message = "must not be null")
    @DecimalMin(value = "0")
    @Column("price")
    private BigDecimal price;

    @NotNull(message = "must not be null")
    @Column("brand_size")
    private Size brandSize;

    @Column("date_added")
    private LocalDate dateAdded;

    @Column("date_modified")
    private LocalDate dateModified;

    @Transient
    @JsonIgnoreProperties(value = { "parent", "brands" }, allowSetters = true)
    private Set<BrandCategory> categories = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Brand id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Brand title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Brand keywords(String keywords) {
        this.setKeywords(keywords);
        return this;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return this.description;
    }

    public Brand description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getImage() {
        return this.image;
    }

    public Brand image(byte[] image) {
        this.setImage(image);
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return this.imageContentType;
    }

    public Brand imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Integer getRating() {
        return this.rating;
    }

    public Brand rating(Integer rating) {
        this.setRating(rating);
        return this;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public BrandStatus getStatus() {
        return this.status;
    }

    public Brand status(BrandStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(BrandStatus status) {
        this.status = status;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public Brand price(BigDecimal price) {
        this.setPrice(price);
        return this;
    }

    public void setPrice(BigDecimal price) {
        this.price = price != null ? price.stripTrailingZeros() : null;
    }

    public Size getBrandSize() {
        return this.brandSize;
    }

    public Brand brandSize(Size brandSize) {
        this.setBrandSize(brandSize);
        return this;
    }

    public void setBrandSize(Size brandSize) {
        this.brandSize = brandSize;
    }

    public LocalDate getDateAdded() {
        return this.dateAdded;
    }

    public Brand dateAdded(LocalDate dateAdded) {
        this.setDateAdded(dateAdded);
        return this;
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public LocalDate getDateModified() {
        return this.dateModified;
    }

    public Brand dateModified(LocalDate dateModified) {
        this.setDateModified(dateModified);
        return this;
    }

    public void setDateModified(LocalDate dateModified) {
        this.dateModified = dateModified;
    }

    public Set<BrandCategory> getCategories() {
        return this.categories;
    }

    public void setCategories(Set<BrandCategory> brandCategories) {
        if (this.categories != null) {
            this.categories.forEach(i -> i.removeBrand(this));
        }
        if (brandCategories != null) {
            brandCategories.forEach(i -> i.addBrand(this));
        }
        this.categories = brandCategories;
    }

    public Brand categories(Set<BrandCategory> brandCategories) {
        this.setCategories(brandCategories);
        return this;
    }

    public Brand addCategory(BrandCategory brandCategory) {
        this.categories.add(brandCategory);
        brandCategory.getBrands().add(this);
        return this;
    }

    public Brand removeCategory(BrandCategory brandCategory) {
        this.categories.remove(brandCategory);
        brandCategory.getBrands().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Brand)) {
            return false;
        }
        return id != null && id.equals(((Brand) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Brand{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", keywords='" + getKeywords() + "'" +
            ", description='" + getDescription() + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + getImageContentType() + "'" +
            ", rating=" + getRating() +
            ", status='" + getStatus() + "'" +
            ", price=" + getPrice() +
            ", brandSize='" + getBrandSize() + "'" +
            ", dateAdded='" + getDateAdded() + "'" +
            ", dateModified='" + getDateModified() + "'" +
            "}";
    }
}
