package com.kwetter.profileService.entities;

import com.google.protobuf.ByteString;
import com.kwetter.profileService.proto.ProfileServiceOuterClass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "Profile")
@Getter
@Setter
public class ProfileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Long accountId;

    String name;

    @Column(length = 160)
    String bio;

    String location;

    String website;

    @Lob
    byte[] image;

    //User 1 follows User 2, User 2 does not follow User 1
    @OneToMany(mappedBy = "userFollowing", fetch=FetchType.EAGER) //User 1
    List<FollowUserEntity> following; //Following these users

    @OneToMany(mappedBy = "userFollowed", fetch=FetchType.EAGER) //User 2
    List<FollowUserEntity> followed; //Followed by these users (followers)

    public ProfileEntity() {}

    public ProfileEntity(ProfileServiceOuterClass.Profile profile) {
        this.id = profile.getId();
        this.accountId = profile.getAccountId();
        this.name = profile.getName();
        this.bio = profile.getBio();
        this.location = profile.getLocation();
        this.website = profile.getWebsite();
        this.image = profile.getImage().toByteArray();
    }

    public ProfileServiceOuterClass.Profile toProfileClass() {
        return ProfileServiceOuterClass.Profile.newBuilder()
                .setId(id)
                .setAccountId(accountId)
                .setBio(bio)
                .setName(name)
                .setLocation(location)
                .setWebsite(website)
                .setImage(ByteString.copyFrom(image))
                .build();
    }
}
