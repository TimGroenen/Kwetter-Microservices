package com.kwetter.messageService.repositories;

import com.kwetter.messageService.entities.TweetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<TweetEntity, Long> {
    boolean existsById(Long id);
    TweetEntity getById(Long id);

    @Query( "select o from TweetEntity o where o.profileId in :ids order by o.date desc" )
    List<TweetEntity> getByProfileIds(@Param("ids") List<Long> profileIds);
}
