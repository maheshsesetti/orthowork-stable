package com.orthoworks.common.repository;

import com.orthoworks.common.domain.BrandCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the BrandCategory entity.
 */
@Repository
public interface BrandCategoryRepository extends JpaRepository<BrandCategory, Long> {
    @Query(
        value = "select distinct brandCategory from BrandCategory brandCategory left join fetch brandCategory.brands",
        countQuery = "select count(distinct brandCategory) from BrandCategory brandCategory"
    )
    Page<BrandCategory> findAllWithEagerRelationships(Pageable pageable);

    @Query("select distinct brandCategory from BrandCategory brandCategory left join fetch brandCategory.brands")
    List<BrandCategory> findAllWithEagerRelationships();

    @Query("select brandCategory from BrandCategory brandCategory left join fetch brandCategory.brands where brandCategory.id =:id")
    Optional<BrandCategory> findOneWithEagerRelationships(@Param("id") Long id);
}
