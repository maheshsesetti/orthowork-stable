package com.orthoworks.common.web.rest;

import static com.orthoworks.common.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.common.IntegrationTest;
import com.orthoworks.common.domain.Brand;
import com.orthoworks.common.domain.enumeration.BrandStatus;
import com.orthoworks.common.domain.enumeration.Size;
import com.orthoworks.common.repository.BrandRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BrandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BrandResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_KEYWORDS = "AAAAAAAAAA";
    private static final String UPDATED_KEYWORDS = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    private static final Integer DEFAULT_RATING = 1;
    private static final Integer UPDATED_RATING = 2;

    private static final BrandStatus DEFAULT_STATUS = BrandStatus.AVAILABLE;
    private static final BrandStatus UPDATED_STATUS = BrandStatus.RESTRICTED;

    private static final BigDecimal DEFAULT_PRICE = new BigDecimal(0);
    private static final BigDecimal UPDATED_PRICE = new BigDecimal(1);

    private static final Size DEFAULT_BRAND_SIZE = Size.S;
    private static final Size UPDATED_BRAND_SIZE = Size.M;

    private static final LocalDate DEFAULT_DATE_ADDED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_ADDED = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_DATE_MODIFIED = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_MODIFIED = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/brands";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBrandMockMvc;

    private Brand brand;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createEntity(EntityManager em) {
        Brand brand = new Brand()
            .title(DEFAULT_TITLE)
            .keywords(DEFAULT_KEYWORDS)
            .description(DEFAULT_DESCRIPTION)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE)
            .rating(DEFAULT_RATING)
            .status(DEFAULT_STATUS)
            .price(DEFAULT_PRICE)
            .brandSize(DEFAULT_BRAND_SIZE)
            .dateAdded(DEFAULT_DATE_ADDED)
            .dateModified(DEFAULT_DATE_MODIFIED);
        return brand;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Brand createUpdatedEntity(EntityManager em) {
        Brand brand = new Brand()
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .price(UPDATED_PRICE)
            .brandSize(UPDATED_BRAND_SIZE)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);
        return brand;
    }

    @BeforeEach
    public void initTest() {
        brand = createEntity(em);
    }

    @Test
    @Transactional
    void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().size();
        // Create the Brand
        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isCreated());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate + 1);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBrand.getKeywords()).isEqualTo(DEFAULT_KEYWORDS);
        assertThat(testBrand.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBrand.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testBrand.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
        assertThat(testBrand.getRating()).isEqualTo(DEFAULT_RATING);
        assertThat(testBrand.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBrand.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testBrand.getBrandSize()).isEqualTo(DEFAULT_BRAND_SIZE);
        assertThat(testBrand.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrand.getDateModified()).isEqualTo(DEFAULT_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void createBrandWithExistingId() throws Exception {
        // Create the Brand with an existing ID
        brand.setId(1L);

        int databaseSizeBeforeCreate = brandRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().size();
        // set the field null
        brand.setTitle(null);

        // Create the Brand, which fails.

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().size();
        // set the field null
        brand.setPrice(null);

        // Create the Brand, which fails.

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkBrandSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().size();
        // set the field null
        brand.setBrandSize(null);

        // Create the Brand, which fails.

        restBrandMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isBadRequest());

        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllBrands() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get all the brandList
        restBrandMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(brand.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].keywords").value(hasItem(DEFAULT_KEYWORDS)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))))
            .andExpect(jsonPath("$.[*].rating").value(hasItem(DEFAULT_RATING)))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(sameNumber(DEFAULT_PRICE))))
            .andExpect(jsonPath("$.[*].brandSize").value(hasItem(DEFAULT_BRAND_SIZE.toString())))
            .andExpect(jsonPath("$.[*].dateAdded").value(hasItem(DEFAULT_DATE_ADDED.toString())))
            .andExpect(jsonPath("$.[*].dateModified").value(hasItem(DEFAULT_DATE_MODIFIED.toString())));
    }

    @Test
    @Transactional
    void getBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        // Get the brand
        restBrandMockMvc
            .perform(get(ENTITY_API_URL_ID, brand.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(brand.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.keywords").value(DEFAULT_KEYWORDS))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .andExpect(jsonPath("$.rating").value(DEFAULT_RATING))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.price").value(sameNumber(DEFAULT_PRICE)))
            .andExpect(jsonPath("$.brandSize").value(DEFAULT_BRAND_SIZE.toString()))
            .andExpect(jsonPath("$.dateAdded").value(DEFAULT_DATE_ADDED.toString()))
            .andExpect(jsonPath("$.dateModified").value(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    @Transactional
    void getNonExistingBrand() throws Exception {
        // Get the brand
        restBrandMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).get();
        // Disconnect from session so that the updates on updatedBrand are not directly saved in db
        em.detach(updatedBrand);
        updatedBrand
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .price(UPDATED_PRICE)
            .brandSize(UPDATED_BRAND_SIZE)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedBrand.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedBrand))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBrand.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testBrand.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrand.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testBrand.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testBrand.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testBrand.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBrand.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testBrand.getBrandSize()).isEqualTo(UPDATED_BRAND_SIZE);
        assertThat(testBrand.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrand.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void putNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, brand.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brand))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(brand))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand using partial update
        Brand partialUpdatedBrand = new Brand();
        partialUpdatedBrand.setId(brand.getId());

        partialUpdatedBrand
            .keywords(UPDATED_KEYWORDS)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .rating(UPDATED_RATING)
            .brandSize(UPDATED_BRAND_SIZE)
            .dateModified(UPDATED_DATE_MODIFIED);

        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrand))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testBrand.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testBrand.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testBrand.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testBrand.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testBrand.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testBrand.getStatus()).isEqualTo(DEFAULT_STATUS);
        assertThat(testBrand.getPrice()).isEqualByComparingTo(DEFAULT_PRICE);
        assertThat(testBrand.getBrandSize()).isEqualTo(UPDATED_BRAND_SIZE);
        assertThat(testBrand.getDateAdded()).isEqualTo(DEFAULT_DATE_ADDED);
        assertThat(testBrand.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void fullUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeUpdate = brandRepository.findAll().size();

        // Update the brand using partial update
        Brand partialUpdatedBrand = new Brand();
        partialUpdatedBrand.setId(brand.getId());

        partialUpdatedBrand
            .title(UPDATED_TITLE)
            .keywords(UPDATED_KEYWORDS)
            .description(UPDATED_DESCRIPTION)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE)
            .rating(UPDATED_RATING)
            .status(UPDATED_STATUS)
            .price(UPDATED_PRICE)
            .brandSize(UPDATED_BRAND_SIZE)
            .dateAdded(UPDATED_DATE_ADDED)
            .dateModified(UPDATED_DATE_MODIFIED);

        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedBrand))
            )
            .andExpect(status().isOk());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
        Brand testBrand = brandList.get(brandList.size() - 1);
        assertThat(testBrand.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testBrand.getKeywords()).isEqualTo(UPDATED_KEYWORDS);
        assertThat(testBrand.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testBrand.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testBrand.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
        assertThat(testBrand.getRating()).isEqualTo(UPDATED_RATING);
        assertThat(testBrand.getStatus()).isEqualTo(UPDATED_STATUS);
        assertThat(testBrand.getPrice()).isEqualByComparingTo(UPDATED_PRICE);
        assertThat(testBrand.getBrandSize()).isEqualTo(UPDATED_BRAND_SIZE);
        assertThat(testBrand.getDateAdded()).isEqualTo(UPDATED_DATE_ADDED);
        assertThat(testBrand.getDateModified()).isEqualTo(UPDATED_DATE_MODIFIED);
    }

    @Test
    @Transactional
    void patchNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, brand.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brand))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(brand))
            )
            .andExpect(status().isBadRequest());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBrandMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(brand)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBrand() throws Exception {
        // Initialize the database
        brandRepository.saveAndFlush(brand);

        int databaseSizeBeforeDelete = brandRepository.findAll().size();

        // Delete the brand
        restBrandMockMvc
            .perform(delete(ENTITY_API_URL_ID, brand.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Brand> brandList = brandRepository.findAll();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
