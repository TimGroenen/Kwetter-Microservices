package com.kwetter.messageService.repositories;

import com.kwetter.messageService.entities.TweetLikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetLikedRepository extends JpaRepository<TweetLikedEntity, Long> {
}
