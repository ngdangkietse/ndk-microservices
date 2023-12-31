package dev.ngdangkiet.repository;

import dev.ngdangkiet.domain.UserTrackingDataActivity;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ngdangkiet
 * @since 11/21/2023
 */

@Repository
public interface UserTrackingDataActivityRepository extends ElasticsearchRepository<UserTrackingDataActivity, Long> {
}
