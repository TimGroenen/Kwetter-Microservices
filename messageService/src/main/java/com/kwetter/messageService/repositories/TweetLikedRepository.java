package com.kwetter.messageService.repositories;

import com.kwetter.messageService.entities.TweetEntity;
import com.kwetter.messageService.entities.TweetLikedEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TweetLikedRepository extends JpaRepository<TweetLikedEntity, Long> {
    boolean existsByProfileIdAndTweetId(Long profileId, Long tweetId);
    int countByTweetId(Long id);
    TweetLikedEntity getByProfileIdAndTweetId(Long profileId, Long tweetId);
}
