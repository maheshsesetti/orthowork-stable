package com.orthoworks.api.repository;

import com.orthoworks.api.domain.Data;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Data entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DataRepository extends JpaRepository<Data, Long> {}
