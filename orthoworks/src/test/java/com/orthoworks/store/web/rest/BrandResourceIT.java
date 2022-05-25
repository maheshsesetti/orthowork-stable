package com.orthoworks.store.web.rest;

import static com.orthoworks.store.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Brand;
import com.orthoworks.store.domain.enumeration.BrandStatus;
import com.orthoworks.store.domain.enumeration.Size;
import com.orthoworks.store.repository.BrandRepository;
import com.orthoworks.store.repository.EntityManager;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link BrandResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
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
    private WebTestClient webTestClient;

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

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Brand.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        brand = createEntity(em);
    }

    @Test
    void createBrand() throws Exception {
        int databaseSizeBeforeCreate = brandRepository.findAll().collectList().block().size();
        // Create the Brand
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
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
    void createBrandWithExistingId() throws Exception {
        // Create the Brand with an existing ID
        brand.setId(1L);

        int databaseSizeBeforeCreate = brandRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().collectList().block().size();
        // set the field null
        brand.setTitle(null);

        // Create the Brand, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPriceIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().collectList().block().size();
        // set the field null
        brand.setPrice(null);

        // Create the Brand, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkBrandSizeIsRequired() throws Exception {
        int databaseSizeBeforeTest = brandRepository.findAll().collectList().block().size();
        // set the field null
        brand.setBrandSize(null);

        // Create the Brand, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllBrands() {
        // Initialize the database
        brandRepository.save(brand).block();

        // Get all the brandList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(brand.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].keywords")
            .value(hasItem(DEFAULT_KEYWORDS))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].imageContentType")
            .value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.[*].image")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.[*].rating")
            .value(hasItem(DEFAULT_RATING))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].price")
            .value(hasItem(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.[*].brandSize")
            .value(hasItem(DEFAULT_BRAND_SIZE.toString()))
            .jsonPath("$.[*].dateAdded")
            .value(hasItem(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.[*].dateModified")
            .value(hasItem(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    void getBrand() {
        // Initialize the database
        brandRepository.save(brand).block();

        // Get the brand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, brand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(brand.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.keywords")
            .value(is(DEFAULT_KEYWORDS))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.imageContentType")
            .value(is(DEFAULT_IMAGE_CONTENT_TYPE))
            .jsonPath("$.image")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGE)))
            .jsonPath("$.rating")
            .value(is(DEFAULT_RATING))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.price")
            .value(is(sameNumber(DEFAULT_PRICE)))
            .jsonPath("$.brandSize")
            .value(is(DEFAULT_BRAND_SIZE.toString()))
            .jsonPath("$.dateAdded")
            .value(is(DEFAULT_DATE_ADDED.toString()))
            .jsonPath("$.dateModified")
            .value(is(DEFAULT_DATE_MODIFIED.toString()));
    }

    @Test
    void getNonExistingBrand() {
        // Get the brand
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewBrand() throws Exception {
        // Initialize the database
        brandRepository.save(brand).block();

        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();

        // Update the brand
        Brand updatedBrand = brandRepository.findById(brand.getId()).block();
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

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedBrand.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
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
    void putNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, brand.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        brandRepository.save(brand).block();

        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();

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

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
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
    void fullUpdateBrandWithPatch() throws Exception {
        // Initialize the database
        brandRepository.save(brand).block();

        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();

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

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedBrand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedBrand))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
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
    void patchNonExistingBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, brand.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamBrand() throws Exception {
        int databaseSizeBeforeUpdate = brandRepository.findAll().collectList().block().size();
        brand.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(brand))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Brand in the database
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteBrand() {
        // Initialize the database
        brandRepository.save(brand).block();

        int databaseSizeBeforeDelete = brandRepository.findAll().collectList().block().size();

        // Delete the brand
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, brand.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Brand> brandList = brandRepository.findAll().collectList().block();
        assertThat(brandList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
