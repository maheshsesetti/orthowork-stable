package com.orthoworks.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orthoworks.common.domain.enumeration.BrandStatus;
import com.orthoworks.common.domain.enumeration.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A Brand.
 */
@Entity
@Table(name = "brand")
public class Brand implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "keywords")
    private String keywords;

    @Column(name = "description")
    private String description;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    @Column(name = "rating")
    private Integer rating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BrandStatus status;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "price", precision = 21, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "brand_size", nullable = false)
    private Size brandSize;

    @Column(name = "date_added")
    private LocalDate dateAdded;

    @Column(name = "date_modified")
    private LocalDate dateModified;

    @ManyToMany(mappedBy = "brands")
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
        this.price = price;
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
