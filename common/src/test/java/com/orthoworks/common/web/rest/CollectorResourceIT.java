package com.orthoworks.common.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.orthoworks.common.IntegrationTest;
import com.orthoworks.common.domain.Collector;
import com.orthoworks.common.domain.enumeration.Gender;
import com.orthoworks.common.repository.CollectorRepository;
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
 * Integration tests for the {@link CollectorResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
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
    private MockMvc restCollectorMockMvc;

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

    @BeforeEach
    public void initTest() {
        collector = createEntity(em);
    }

    @Test
    @Transactional
    void createCollector() throws Exception {
        int databaseSizeBeforeCreate = collectorRepository.findAll().size();
        // Create the Collector
        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isCreated());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
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
    @Transactional
    void createCollectorWithExistingId() throws Exception {
        // Create the Collector with an existing ID
        collector.setId(1L);

        int databaseSizeBeforeCreate = collectorRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setFirstName(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setLastName(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setGender(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setEmail(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setPhone(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setAddressLine1(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setCity(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = collectorRepository.findAll().size();
        // set the field null
        collector.setCountry(null);

        // Create the Collector, which fails.

        restCollectorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isBadRequest());

        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllCollectors() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        // Get all the collectorList
        restCollectorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(collector.getId().intValue())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)));
    }

    @Test
    @Transactional
    void getCollector() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        // Get the collector
        restCollectorMockMvc
            .perform(get(ENTITY_API_URL_ID, collector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(collector.getId().intValue()))
            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phone").value(DEFAULT_PHONE))
            .andExpect(jsonPath("$.addressLine1").value(DEFAULT_ADDRESS_LINE_1))
            .andExpect(jsonPath("$.addressLine2").value(DEFAULT_ADDRESS_LINE_2))
            .andExpect(jsonPath("$.city").value(DEFAULT_CITY))
            .andExpect(jsonPath("$.country").value(DEFAULT_COUNTRY));
    }

    @Test
    @Transactional
    void getNonExistingCollector() throws Exception {
        // Get the collector
        restCollectorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewCollector() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();

        // Update the collector
        Collector updatedCollector = collectorRepository.findById(collector.getId()).get();
        // Disconnect from session so that the updates on updatedCollector are not directly saved in db
        em.detach(updatedCollector);
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

        restCollectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedCollector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedCollector))
            )
            .andExpect(status().isOk());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
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
    @Transactional
    void putNonExistingCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, collector.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(collector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(collector)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCollectorWithPatch() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();

        // Update the collector using partial update
        Collector partialUpdatedCollector = new Collector();
        partialUpdatedCollector.setId(collector.getId());

        partialUpdatedCollector.gender(UPDATED_GENDER).addressLine1(UPDATED_ADDRESS_LINE_1).addressLine2(UPDATED_ADDRESS_LINE_2);

        restCollectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollector))
            )
            .andExpect(status().isOk());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
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
    @Transactional
    void fullUpdateCollectorWithPatch() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();

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

        restCollectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedCollector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedCollector))
            )
            .andExpect(status().isOk());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
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
    @Transactional
    void patchNonExistingCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, collector.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(collector))
            )
            .andExpect(status().isBadRequest());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamCollector() throws Exception {
        int databaseSizeBeforeUpdate = collectorRepository.findAll().size();
        collector.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCollectorMockMvc
            .perform(
                patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(collector))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the Collector in the database
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteCollector() throws Exception {
        // Initialize the database
        collectorRepository.saveAndFlush(collector);

        int databaseSizeBeforeDelete = collectorRepository.findAll().size();

        // Delete the collector
        restCollectorMockMvc
            .perform(delete(ENTITY_API_URL_ID, collector.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Collector> collectorList = collectorRepository.findAll();
        assertThat(collectorList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
