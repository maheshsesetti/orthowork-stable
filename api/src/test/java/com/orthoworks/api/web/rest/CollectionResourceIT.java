package com.orthoworks.api.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.api.IntegrationTest;
import com.orthoworks.api.domain.Collection;
import com.orthoworks.api.domain.enumeration.AuctionType;
import com.orthoworks.api.domain.enumeration.CollectionType;
import com.orthoworks.api.domain.enumeration.Currency;
import com.orthoworks.api.repository.CollectionRepository;
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

/**
 * Integration tests for the {@link CollectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restCollectionMockMvc;

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

    @BeforeEach
    public void initTest() {
        collection = createEntity(em);
    }

    @Test
    @Transactional
    void createCollection() throws Exception {
        int databaseSizeBeforeCreate = collectionRepository.findAll().size();
        // Create the Collection
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isCreated());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
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
    @Transactional
    void createCollectionWithExistingId() throws Exception {
        // Create the Collection with an existing ID
        collection.setId(1L);

        int databaseSizeBeforeCreate = collectionRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setName(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setTitle(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCollectionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setCollectionType(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAuctionTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setAuctionType(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMinRangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setMinRange(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkMaxRangeIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setMaxRange(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCurrencyIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setCurrency(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkOwnerIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectionRepository.findAll().size();
        // set the field null
        collection.setOwner(null);

        // Create the Collection, which fails.

        restCollectionMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isBadRequest());

        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCollections() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        // Get all the collectionList
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collection.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].count").value(hasItem(DEFAULT_COUNT)))
            .andExpect(jsonPath("$.[*].collectionType").value(hasItem(DEFAULT_COLLECTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].auctionType").value(hasItem(DEFAULT_AUCTION_TYPE.toString())))
            .andExpect(jsonPath("$.[*].minRange").value(hasItem(DEFAULT_MIN_RANGE.doubleValue())))
            .andExpect(jsonPath("$.[*].maxRange").value(hasItem(DEFAULT_MAX_RANGE.doubleValue())))
            .andExpect(jsonPath("$.[*].currency").value(hasItem(DEFAULT_CURRENCY.toString())))
            .andExpect(jsonPath("$.[*].owner").value(hasItem(DEFAULT_OWNER)));
    }

    @Test
    @Transactional
    void getCollection() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        // Get the collection
        restCollectionMockMvc
            .perform(get(ENTITY_API_URL_ID, collection.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collection.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.count").value(DEFAULT_COUNT))
            .andExpect(jsonPath("$.collectionType").value(DEFAULT_COLLECTION_TYPE.toString()))
            .andExpect(jsonPath("$.auctionType").value(DEFAULT_AUCTION_TYPE.toString()))
            .andExpect(jsonPath("$.minRange").value(DEFAULT_MIN_RANGE.doubleValue()))
            .andExpect(jsonPath("$.maxRange").value(DEFAULT_MAX_RANGE.doubleValue()))
            .andExpect(jsonPath("$.currency").value(DEFAULT_CURRENCY.toString()))
            .andExpect(jsonPath("$.owner").value(DEFAULT_OWNER));
    }

    @Test
    @Transactional
    void getNonExistingCollection() throws Exception {
        // Get the collection
        restCollectionMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCollection() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

        // Update the collection
        Collection updatedCollection = collectionRepository.findById(collection.getId()).get();
        // Disconnect from session so that the updates on updatedCollection are not directly saved in db
        em.detach(updatedCollection);
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

        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCollection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
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
    @Transactional
    void putNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collection.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collection)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

        // Update the collection using partial update
        Collection partialUpdatedCollection = new Collection();
        partialUpdatedCollection.setId(collection.getId());

        partialUpdatedCollection.name(UPDATED_NAME).minRange(UPDATED_MIN_RANGE).owner(UPDATED_OWNER);

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
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
    @Transactional
    void fullUpdateCollectionWithPatch() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();

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

        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollection))
            )
            .andExpect(status().isOk());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
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
    @Transactional
    void patchNonExistingCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collection.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollection() throws Exception {
        int databaseSizeBeforeUpdate = collectionRepository.findAll().size();
        collection.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectionMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(collection))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collection in the database
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollection() throws Exception {
        // Initialize the database
        collectionRepository.saveAndFlush(collection);

        int databaseSizeBeforeDelete = collectionRepository.findAll().size();

        // Delete the collection
        restCollectionMockMvc
            .perform(delete(ENTITY_API_URL_ID, collection.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Collection> collectionList = collectionRepository.findAll();
        assertThat(collectionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
