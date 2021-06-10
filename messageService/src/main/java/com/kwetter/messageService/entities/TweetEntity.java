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

    public TweetEntity(Long profileId, String content) {
        this.profileId = profileId;
        this.content = content;
        this.date = new Date();
    }

    public TweetEntity(MessageServiceOuterClass.Tweet tweet) {
        this.id = tweet.getId();
        this.profileId = tweet.getProfileId();
        this.content = tweet.getContent();
        this.date = new Date(tweet.getDate() * 1000L); //Convert from seconds to milliseconds
    }

    public MessageServiceOuterClass.Tweet toTweetClass() {
        return MessageServiceOuterClass.Tweet.newBuilder().setId(id).setProfileId(profileId).setContent(content).setDate(date.getTime()/1000).setIsLiked(false).setLikes(0).build();
    }

    public MessageServiceOuterClass.Tweet toTweetClass(boolean isLiked, int likes) {
        return MessageServiceOuterClass.Tweet.newBuilder().setId(id).setProfileId(profileId).setContent(content).setDate(date.getTime()/1000).setIsLiked(isLiked).setLikes(likes).build();
    }
}
