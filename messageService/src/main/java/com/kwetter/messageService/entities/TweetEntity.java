package com.kwetter.messageService.entities;

import com.kwetter.messageService.proto.MessageServiceOuterClass;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Tweet")
@Getter
@Setter
public class TweetEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long profileId;

    String content;

    Date date;

    @OneToMany(mappedBy = "tweet", fetch = FetchType.EAGER)
    List<TweetLikedEntity> likes;

    public TweetEntity() {}

    public TweetEntity(MessageServiceOuterClass.Tweet tweet) {
        this.id = tweet.getId();
        this.profileId = tweet.getProfileId();
        this.content = tweet.getContent();
        this.date = new Date(tweet.getDate() * 1000L); //Convert from seconds to milliseconds
    }
}
