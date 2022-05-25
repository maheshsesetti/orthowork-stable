package com.orthoworks.api.repository;

import java.util.Optional;

import com.orthoworks.api.domain.Collection;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Collection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CollectionRepository extends JpaRepository<Collection, Long> {
    @Query(value = "select c from collection c where c.name = :name", nativeQuery = true)
    Optional<Collection> findBySlug(@Param("name") String id);
}
