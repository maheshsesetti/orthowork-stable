package com.orthoworks.api.repository;

import com.orthoworks.api.domain.Feature;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Feature entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeatureRepository extends JpaRepository<Feature, Long> {}
