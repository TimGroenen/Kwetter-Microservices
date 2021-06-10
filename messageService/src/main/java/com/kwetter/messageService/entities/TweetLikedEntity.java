package com.kwetter.messageService.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "TweetLiked")
@Getter
@Setter
public class TweetLikedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long profileId;

    @ManyToOne
    TweetEntity tweet;

    public TweetLikedEntity() {}

    public TweetLikedEntity(Long profileId, TweetEntity tweet) {
        this.profileId = profileId;
        this.tweet = tweet;
    }
}
