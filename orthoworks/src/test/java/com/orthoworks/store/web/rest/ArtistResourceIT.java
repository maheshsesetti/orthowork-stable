package com.orthoworks.store.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.orthoworks.store.IntegrationTest;
import com.orthoworks.store.domain.Artist;
import com.orthoworks.store.domain.enumeration.Gender;
import com.orthoworks.store.repository.ArtistRepository;
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
 * Integration tests for the {@link ArtistResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ArtistResourceIT {

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final Gender DEFAULT_GENDER = Gender.MALE;
    private static final Gender UPDATED_GENDER = Gender.FEMALE;

    private static final String DEFAULT_EMAIL = "v@w..</";
    private static final String UPDATED_EMAIL = "IBrt@)h.c";

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

    private static final String ENTITY_API_URL = "/api/artists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Artist artist;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createEntity(EntityManager em) {
        Artist artist = new Artist()
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .gender(DEFAULT_GENDER)
            .email(DEFAULT_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY);
        return artist;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Artist createUpdatedEntity(EntityManager em) {
        Artist artist = new Artist()
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .gender(UPDATED_GENDER)
            .email(UPDATED_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY);
        return artist;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Artist.class).block();
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
        artist = createEntity(em);
    }

    @Test
    void createArtist() throws Exception {
        int databaseSizeBeforeCreate = artistRepository.findAll().collectList().block().size();
        // Create the Artist
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeCreate + 1);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testArtist.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testArtist.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testArtist.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testArtist.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testArtist.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testArtist.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testArtist.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testArtist.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void createArtistWithExistingId() throws Exception {
        // Create the Artist with an existing ID
        artist.setId(1L);

        int databaseSizeBeforeCreate = artistRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkFirstNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setFirstName(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkLastNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setLastName(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setGender(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setEmail(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkPhoneIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setPhone(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkAddressLine1IsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setAddressLine1(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCityIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setCity(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void checkCountryIsRequired() throws Exception {
        int databaseSizeBeforeTest = artistRepository.findAll().collectList().block().size();
        // set the field null
        artist.setCountry(null);

        // Create the Artist, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllArtistsAsStream() {
        // Initialize the database
        artistRepository.save(artist).block();

        List<Artist> artistList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Artist.class)
            .getResponseBody()
            .filter(artist::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(artistList).isNotNull();
        assertThat(artistList).hasSize(1);
        Artist testArtist = artistList.get(0);
        assertThat(testArtist.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testArtist.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testArtist.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testArtist.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testArtist.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testArtist.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testArtist.getAddressLine2()).isEqualTo(DEFAULT_ADDRESS_LINE_2);
        assertThat(testArtist.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testArtist.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void getAllArtists() {
        // Initialize the database
        artistRepository.save(artist).block();

        // Get all the artistList
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
            .value(hasItem(artist.getId().intValue()))
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
    void getArtist() {
        // Initialize the database
        artistRepository.save(artist).block();

        // Get the artist
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(artist.getId().intValue()))
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
    void getNonExistingArtist() {
        // Get the artist
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewArtist() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist
        Artist updatedArtist = artistRepository.findById(artist.getId()).block();
        updatedArtist
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
            .uri(ENTITY_API_URL_ID, updatedArtist.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testArtist.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testArtist.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testArtist.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testArtist.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testArtist.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testArtist.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testArtist.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testArtist.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    void putNonExistingArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        partialUpdatedArtist.addressLine2(UPDATED_ADDRESS_LINE_2);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
        assertThat(testArtist.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
        assertThat(testArtist.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testArtist.getEmail()).isEqualTo(DEFAULT_EMAIL);
        assertThat(testArtist.getPhone()).isEqualTo(DEFAULT_PHONE);
        assertThat(testArtist.getAddressLine1()).isEqualTo(DEFAULT_ADDRESS_LINE_1);
        assertThat(testArtist.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testArtist.getCity()).isEqualTo(DEFAULT_CITY);
        assertThat(testArtist.getCountry()).isEqualTo(DEFAULT_COUNTRY);
    }

    @Test
    void fullUpdateArtistWithPatch() throws Exception {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();

        // Update the artist using partial update
        Artist partialUpdatedArtist = new Artist();
        partialUpdatedArtist.setId(artist.getId());

        partialUpdatedArtist
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
            .uri(ENTITY_API_URL_ID, partialUpdatedArtist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedArtist))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
        Artist testArtist = artistList.get(artistList.size() - 1);
        assertThat(testArtist.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
        assertThat(testArtist.getLastName()).isEqualTo(UPDATED_LAST_NAME);
        assertThat(testArtist.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testArtist.getEmail()).isEqualTo(UPDATED_EMAIL);
        assertThat(testArtist.getPhone()).isEqualTo(UPDATED_PHONE);
        assertThat(testArtist.getAddressLine1()).isEqualTo(UPDATED_ADDRESS_LINE_1);
        assertThat(testArtist.getAddressLine2()).isEqualTo(UPDATED_ADDRESS_LINE_2);
        assertThat(testArtist.getCity()).isEqualTo(UPDATED_CITY);
        assertThat(testArtist.getCountry()).isEqualTo(UPDATED_COUNTRY);
    }

    @Test
    void patchNonExistingArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamArtist() throws Exception {
        int databaseSizeBeforeUpdate = artistRepository.findAll().collectList().block().size();
        artist.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(artist))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Artist in the database
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteArtist() {
        // Initialize the database
        artistRepository.save(artist).block();

        int databaseSizeBeforeDelete = artistRepository.findAll().collectList().block().size();

        // Delete the artist
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, artist.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Artist> artistList = artistRepository.findAll().collectList().block();
        assertThat(artistList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
