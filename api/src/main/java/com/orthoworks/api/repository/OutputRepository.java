package com.orthoworks.api.repository;

import com.orthoworks.api.domain.Output;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Output entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OutputRepository extends JpaRepository<Output, Long> {}
