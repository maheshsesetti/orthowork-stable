package com.orthoworks.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.orthoworks.common.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CollectorTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Collector.class);
        Collector collector1 = new Collector();
        collector1.setId(1L);
        Collector collector2 = new Collector();
        collector2.setId(collector1.getId());
        assertThat(collector1).isEqualTo(collector2);
        collector2.setId(2L);
        assertThat(collector1).isNotEqualTo(collector2);
        collector1.setId(null);
        assertThat(collector1).isNotEqualTo(collector2);
    }
}
