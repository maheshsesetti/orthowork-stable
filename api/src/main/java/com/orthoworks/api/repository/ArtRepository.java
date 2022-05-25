package com.orthoworks.api.repository;

import com.orthoworks.api.domain.Art;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Art entity.
 */
@Repository
public interface ArtRepository extends JpaRepository<Art, Long> {
    @Query(
        value = "select distinct art from Art art left join fetch art.collections",
        countQuery = "select count(distinct art) from Art art"
    )
    Page<Art> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct art from Art art left join fetch art.collections")
    List<Art> findAllWithEagerRelationships();

    @Query("select art from Art art left join fetch art.collections where art.id =:id")
    Optional<Art> findOneWithEagerRelationships(@Param("id") Long id);
}
