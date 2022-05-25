package com.orthoworks.common.repository;

import com.orthoworks.common.domain.Collector;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Collector entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollectorRepository extends JpaRepository<Collector, Long> {}
