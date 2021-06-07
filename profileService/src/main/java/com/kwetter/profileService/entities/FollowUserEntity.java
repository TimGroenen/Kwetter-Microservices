package com.kwetter.profileService.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "FollowUser")
@Getter
@Setter
public class FollowUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //User 1 follows User 2, User 2 does not follow User 1
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userFollowingId", nullable=false)
    ProfileEntity userFollowing; //User 1

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="userFollowedId", nullable=false)
    ProfileEntity userFollowed; //User 2

    public FollowUserEntity() {}

    public FollowUserEntity(ProfileEntity userFollowing, ProfileEntity userFollowed) {
        this.userFollowing = userFollowing;
        this.userFollowed = userFollowed;
    }
}
