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

    @ManyToOne
    @JoinColumn(name="userFollowingId", nullable=false)
    ProfileEntity userFollowing;

    @ManyToOne
    @JoinColumn(name="userFollowedId", nullable=false)
    ProfileEntity userFollowed;

    public FollowUserEntity() {}

    public FollowUserEntity(ProfileEntity userFollowing, ProfileEntity userFollowed) {
        this.userFollowing = userFollowing;
        this.userFollowed = userFollowed;
    }
}
