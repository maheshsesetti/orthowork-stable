package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Collector;
import com.orthoworks.store.domain.enumeration.Gender;
import com.orthoworks.store.repository.CollectorRepository;
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
 * Integration tests for the {@link CollectorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CollectorResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "=@Gx.fD";
    private static final String UPDATED_EMAIL = "rpK~F@h\"obt0./1";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/collectors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CollectorRepository collectorRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Collector collector;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collector createEntity(EntityManager em) {
        Collector collector = new Collector()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY);
        return collector;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Collector createUpdatedEntity(EntityManager em) {
        Collector collector = new Collector()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY);
        return collector;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Collector.class).block();
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
        collector = createEntity(em);
    }

    @Test
    void createCollector() throws Exception {
        int databaseSizeBeforeCreate = collectorRepository.findAll().collectList().block().size();
        // Create the Collector
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeCreate + 1);
        Collector testCollector = collectorList.get(collectorList.size() - 1);
        assertThat(testCollector.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCollector.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCollector.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testCollector.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCollector.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCollector.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testCollector.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testCollector.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCollector.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void createCollectorWithExistingId() throws Exception {
        // Create the Collector with an existing ID
        collector.setId(1L);

        int databaseSizeBeforeCreate = collectorRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setFirstName(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setLastName(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setGender(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setEmail(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setPhone(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setAddressLine1(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setCity(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().collectList().block().size();
        // set the field null
        collector.setCountry(null);

        // Create the Collector, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllCollectorsAsStream() {
        // Initialize the database
        collectorRepository.save(collector).block();

        List<Collector> collectorList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Collector.class)
            .getResponseBody()
            .filter(collector::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(collectorList).isNotNull();
        assertThat(collectorList).hasSize(1);
        Collector testCollector = collectorList.get(0);
        assertThat(testCollector.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCollector.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCollector.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testCollector.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCollector.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCollector.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testCollector.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testCollector.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCollector.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void getAllCollectors() {
        // Initialize the database
        collectorRepository.save(collector).block();

        // Get all the collectorList
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
            .value(hasItem(collector.getId().intValue()))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].gender")
            .value(hasItem(DEFAULT_GENDER.toString()))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].phone")
            .value(hasItem(DEFAULT_PHONE))
            .jsonPath("$.[*].addressLine1")
            .value(hasItem(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.[*].addressLine2")
            .value(hasItem(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.[*].city")
            .value(hasItem(DEFAULT_CITY))
            .jsonPath("$.[*].country")
            .value(hasItem(DEFAULT_COUNTRY));
    }

    @Test
    void getCollector() {
        // Initialize the database
        collectorRepository.save(collector).block();

        // Get the collector
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, collector.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(collector.getId().intValue()))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.gender")
            .value(is(DEFAULT_GENDER.toString()))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.phone")
            .value(is(DEFAULT_PHONE))
            .jsonPath("$.addressLine1")
            .value(is(DEFAULT_ADDRESS_LINE_1))
            .jsonPath("$.addressLine2")
            .value(is(DEFAULT_ADDRESS_LINE_2))
            .jsonPath("$.city")
            .value(is(DEFAULT_CITY))
            .jsonPath("$.country")
            .value(is(DEFAULT_COUNTRY));
    }

    @Test
    void getNonExistingCollector() {
        // Get the collector
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCollector() throws Exception {
        // Initialize the database
        collectorRepository.save(collector).block();

        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();

        // Update the collector
        Collector updatedCollector = collectorRepository.findById(collector.getId()).block();
        updatedCollector
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCollector.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCollector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
        Collector testCollector = collectorList.get(collectorList.size() - 1);
        assertThat(testCollector.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCollector.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCollector.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCollector.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCollector.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCollector.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCollector.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCollector.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCollector.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    void putNonExistingCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, collector.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCollectorWithPatch() throws Exception {
        // Initialize the database
        collectorRepository.save(collector).block();

        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();

        // Update the collector using partial update
        Collector partialUpdatedCollector = new Collector();
        partialUpdatedCollector.setId(collector.getId());

        partialUpdatedCollector.gender(UPDATED_GENDER).addressLine1(UPDATED_ADDRESS_LINE_1).addressLine2(UPDATED_ADDRESS_LINE_2);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCollector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCollector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
        Collector testCollector = collectorList.get(collectorList.size() - 1);
        assertThat(testCollector.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testCollector.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testCollector.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCollector.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testCollector.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testCollector.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCollector.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCollector.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testCollector.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void fullUpdateCollectorWithPatch() throws Exception {
        // Initialize the database
        collectorRepository.save(collector).block();

        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();

        // Update the collector using partial update
        Collector partialUpdatedCollector = new Collector();
        partialUpdatedCollector.setId(collector.getId());

        partialUpdatedCollector
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCollector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCollector))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
        Collector testCollector = collectorList.get(collectorList.size() - 1);
        assertThat(testCollector.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testCollector.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testCollector.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testCollector.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testCollector.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testCollector.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testCollector.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testCollector.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testCollector.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    void patchNonExistingCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, collector.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().collectList().block().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(collector))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCollector() {
        // Initialize the database
        collectorRepository.save(collector).block();

        int databaseSizeBeforeDelete = collectorRepository.findAll().collectList().block().size();

        // Delete the collector
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, collector.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Collector> collectorList = collectorRepository.findAll().collectList().block();
        assertThat(collectorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
