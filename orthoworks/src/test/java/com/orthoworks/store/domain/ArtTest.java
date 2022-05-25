package com.orthoworks.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.orthoworks.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArtTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Art.class);
        Art art1 = new Art();
        art1.setId(1L);
        Art art2 = new Art();
        art2.setId(art1.getId());
        assertThat(art1).isEqualTo(art2);
        art2.setId(2L);
        assertThat(art1).isNotEqualTo(art2);
        art1.setId(null);
        assertThat(art1).isNotEqualTo(art2);
    }
}
