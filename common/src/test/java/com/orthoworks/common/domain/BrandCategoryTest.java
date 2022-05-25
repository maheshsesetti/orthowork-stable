package com.orthoworks.common.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.orthoworks.common.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BrandCategoryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BrandCategory.class);
        BrandCategory brandCategory1 = new BrandCategory();
        brandCategory1.setId(1L);
        BrandCategory brandCategory2 = new BrandCategory();
        brandCategory2.setId(brandCategory1.getId());
        assertThat(brandCategory1).isEqualTo(brandCategory2);
        brandCategory2.setId(2L);
        assertThat(brandCategory1).isNotEqualTo(brandCategory2);
        brandCategory1.setId(null);
        assertThat(brandCategory1).isNotEqualTo(brandCategory2);
    }
}
