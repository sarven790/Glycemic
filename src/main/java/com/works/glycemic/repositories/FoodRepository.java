package com.works.glycemic.repositories;

import com.works.glycemic.models.Foods;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Foods, Long> {

    Optional<Foods> findByNameEqualsIgnoreCase(String name);

    List<Foods> findByCreatedByEqualsIgnoreCase(String createdBy);

    Optional<Foods> findByGidAndCreatedByIgnoreCase(Long gid, String createdBy);

    Optional<Foods> findBydetailsUrlEqualsIgnoreCaseAllIgnoreCase(String url);

    List<Foods> findByEnabledEqualsOrderByGidDesc(boolean enabled);

}