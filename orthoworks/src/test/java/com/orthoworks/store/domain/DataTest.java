package com.orthoworks.store.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.orthoworks.store.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DataTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Data.class);
        Data data1 = new Data();
        data1.setId(1L);
        Data data2 = new Data();
        data2.setId(data1.getId());
        assertThat(data1).isEqualTo(data2);
        data2.setId(2L);
        assertThat(data1).isNotEqualTo(data2);
        data1.setId(null);
        assertThat(data1).isNotEqualTo(data2);
    }
}
