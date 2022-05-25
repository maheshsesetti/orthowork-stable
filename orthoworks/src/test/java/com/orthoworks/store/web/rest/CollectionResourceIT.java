package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Collection;
import com.orthoworks.store.domain.enumeration.AuctionType;
import com.orthoworks.store.domain.enumeration.CollectionType;
import com.orthoworks.store.domain.enumeration.Currency;
import com.orthoworks.store.repository.CollectionRepository;
import com.orthoworks.store.repository.EntityManager;
import java.time.Duration;
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

/**
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CollectionResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Integer DEFAULT_COUNT = 1;
    private static final Integer UPDATED_COUNT = 2;

    private static final CollectionType DEFAULT_COLLECTION_TYPE = CollectionType.IMAGE;
    private static final CollectionType UPDATED_COLLECTION_TYPE = CollectionType.AUDIO;

    private static final AuctionType DEFAULT_AUCTION_TYPE = AuctionType.FLAT;
    private static final AuctionType UPDATED_AUCTION_TYPE = AuctionType.ENGLISH;

    private static final Float DEFAULT_MIN_RANGE = 1F;
    private static final Float UPDATED_MIN_RANGE = 2F;

    private static final Float DEFAULT_MAX_RANGE = 1F;
    private static final Float UPDATED_MAX_RANGE = 2F;

    private static final Currency DEFAULT_CURRENCY = Currency.INR;
    private static final Currency UPDATED_CURRENCY = Currency.USD;

    private static final String DEFAULT_OWNER = "AAAAAAAAAA";
    private static final String UPDATED_OWNER = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/collections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CollectionRepository collectionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Collection collection;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createEntity(EntityManager em) {
        Collection collection = new Collection()
            .name(DEFAULT_NAME)
            .title(DEFAULT_TITLE)
            .count(DEFAULT_COUNT)
            .collectionType(DEFAULT_COLLECTION_TYPE)
            .auctionType(DEFAULT_AUCTION_TYPE)
            .minRange(DEFAULT_MIN_RANGE)
            .maxRange(DEFAULT_MAX_RANGE)
            .currency(DEFAULT_CURRENCY)
            .owner(DEFAULT_OWNER);
        return collection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collection createUpdatedEntity(EntityManager em) {
        Collection collection = new Collection()
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .count(UPDATED_COUNT)
            .collectionType(UPDATED_COLLECTION_TYPE)
            .auctionType(UPDATED_AUCTION_TYPE)
            .minRange(UPDATED_MIN_RANGE)
            .maxRange(UPDATED_MAX_RANGE)
            .currency(UPDATED_CURRENCY)
            .owner(UPDATED_OWNER);
        return collection;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Collection.class).block();
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
        collection = createEntity(em);
    }

    @Test
    void createCollection() throws Exception {
        int databaseSizeBeforeCreate = collectionRepository.findAll().collectList().block().size();
        // Create the Collection
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate + 1);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCollection.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testCollection.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testCollection.getCollectionType()).isEqualTo(DEFAULT_COLLECTION_TYPE);
        assertThat(testCollection.getAuctionType()).isEqualTo(DEFAULT_AUCTION_TYPE);
        assertThat(testCollection.getMinRange()).isEqualTo(DEFAULT_MIN_RANGE);
        assertThat(testCollection.getMaxRange()).isEqualTo(DEFAULT_MAX_RANGE);
        assertThat(testCollection.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testCollection.getOwner()).isEqualTo(DEFAULT_OWNER);
    }

    @Test
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collection.setId(1L);

        int databaseSizeBeforeCreate = collectionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setName(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setTitle(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCollectionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setCollectionType(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAuctionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setAuctionType(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMinRangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setMinRange(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkMaxRangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setMaxRange(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setCurrency(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkOwnerIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().collectList().block().size();
        // set the field null
        collection.setOwner(null);

        // Create the Collection, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCollections() {
        // Initialize the database
        collectionRepository.save(collection).block();

        // Get all the collectionList
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
            .value(hasItem(collection.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].count")
            .value(hasItem(DEFAULT_COUNT))
            .jsonPath("$.[*].collectionType")
            .value(hasItem(DEFAULT_COLLECTION_TYPE.toString()))
            .jsonPath("$.[*].auctionType")
            .value(hasItem(DEFAULT_AUCTION_TYPE.toString()))
            .jsonPath("$.[*].minRange")
            .value(hasItem(DEFAULT_MIN_RANGE.doubleValue()))
            .jsonPath("$.[*].maxRange")
            .value(hasItem(DEFAULT_MAX_RANGE.doubleValue()))
            .jsonPath("$.[*].currency")
            .value(hasItem(DEFAULT_CURRENCY.toString()))
            .jsonPath("$.[*].owner")
            .value(hasItem(DEFAULT_OWNER));
    }

    @Test
    void getCollection() {
        // Initialize the database
        collectionRepository.save(collection).block();

        // Get the collection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, collection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(collection.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.count")
            .value(is(DEFAULT_COUNT))
            .jsonPath("$.collectionType")
            .value(is(DEFAULT_COLLECTION_TYPE.toString()))
            .jsonPath("$.auctionType")
            .value(is(DEFAULT_AUCTION_TYPE.toString()))
            .jsonPath("$.minRange")
            .value(is(DEFAULT_MIN_RANGE.doubleValue()))
            .jsonPath("$.maxRange")
            .value(is(DEFAULT_MAX_RANGE.doubleValue()))
            .jsonPath("$.currency")
            .value(is(DEFAULT_CURRENCY.toString()))
            .jsonPath("$.owner")
            .value(is(DEFAULT_OWNER));
    }

    @Test
    void getNonExistingCollection() {
        // Get the collection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCollection() throws Exception {
        // Initialize the database
        collectionRepository.save(collection).block();

        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();

        // Update the collection
        Collection updatedCollection = collectionRepository.findById(collection.getId()).block();
        updatedCollection
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .count(UPDATED_COUNT)
            .collectionType(UPDATED_COLLECTION_TYPE)
            .auctionType(UPDATED_AUCTION_TYPE)
            .minRange(UPDATED_MIN_RANGE)
            .maxRange(UPDATED_MAX_RANGE)
            .currency(UPDATED_CURRENCY)
            .owner(UPDATED_OWNER);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCollection.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCollection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCollection.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testCollection.getCollectionType()).isEqualTo(UPDATED_COLLECTION_TYPE);
        assertThat(testCollection.getAuctionType()).isEqualTo(UPDATED_AUCTION_TYPE);
        assertThat(testCollection.getMinRange()).isEqualTo(UPDATED_MIN_RANGE);
        assertThat(testCollection.getMaxRange()).isEqualTo(UPDATED_MAX_RANGE);
        assertThat(testCollection.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCollection.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    void putNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, collection.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collectionRepository.save(collection).block();

        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection.name(UPDATED_NAME).minRange(UPDATED_MIN_RANGE).owner(UPDATED_OWNER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testCollection.getCount()).isEqualTo(DEFAULT_COUNT);
        assertThat(testCollection.getCollectionType()).isEqualTo(DEFAULT_COLLECTION_TYPE);
        assertThat(testCollection.getAuctionType()).isEqualTo(DEFAULT_AUCTION_TYPE);
        assertThat(testCollection.getMinRange()).isEqualTo(UPDATED_MIN_RANGE);
        assertThat(testCollection.getMaxRange()).isEqualTo(DEFAULT_MAX_RANGE);
        assertThat(testCollection.getCurrency()).isEqualTo(DEFAULT_CURRENCY);
        assertThat(testCollection.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collectionRepository.save(collection).block();

        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection
            .name(UPDATED_NAME)
            .title(UPDATED_TITLE)
            .count(UPDATED_COUNT)
            .collectionType(UPDATED_COLLECTION_TYPE)
            .auctionType(UPDATED_AUCTION_TYPE)
            .minRange(UPDATED_MIN_RANGE)
            .maxRange(UPDATED_MAX_RANGE)
            .currency(UPDATED_CURRENCY)
            .owner(UPDATED_OWNER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
        Collection testCollection = collectionList.get(collectionList.size() - 1);
        assertThat(testCollection.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCollection.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testCollection.getCount()).isEqualTo(UPDATED_COUNT);
        assertThat(testCollection.getCollectionType()).isEqualTo(UPDATED_COLLECTION_TYPE);
        assertThat(testCollection.getAuctionType()).isEqualTo(UPDATED_AUCTION_TYPE);
        assertThat(testCollection.getMinRange()).isEqualTo(UPDATED_MIN_RANGE);
        assertThat(testCollection.getMaxRange()).isEqualTo(UPDATED_MAX_RANGE);
        assertThat(testCollection.getCurrency()).isEqualTo(UPDATED_CURRENCY);
        assertThat(testCollection.getOwner()).isEqualTo(UPDATED_OWNER);
    }

    @Test
    void patchNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, collection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().collectList().block().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collection))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCollection() {
        // Initialize the database
        collectionRepository.save(collection).block();

        int databaseSizeBeforeDelete = collectionRepository.findAll().collectList().block().size();

        // Delete the collection
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, collection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Collection> collectionList = collectionRepository.findAll().collectList().block();
        assertThat(collectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
