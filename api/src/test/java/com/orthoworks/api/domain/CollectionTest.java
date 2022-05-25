package com.orthoworks.api.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.orthoworks.api.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CollectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collection.class);
        Collection collection1 = new Collection();
        collection1.setId(1L);
        Collection collection2 = new Collection();
        collection2.setId(collection1.getId());
        assertThat(collection1).isEqualTo(collection2);
        collection2.setId(2L);
        assertThat(collection1).isNotEqualTo(collection2);
        collection1.setId(null);
        assertThat(collection1).isNotEqualTo(collection2);
    }
}
