package com.kwetter.profileService.entities;

import com.kwetter.profileService.proto.ProfileServiceOuterClass;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

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

    public ProfileEntity() {}

    public ProfileEntity(ProfileServiceOuterClass.Profile profile) {
        this.id = profile.getId();
        this.accountId = profile.getAccountId();
        this.name = profile.getName();
        this.bio = profile.getBio();
        this.location = profile.getLocation();
        this.website = profile.getWebsite();
    }

    public ProfileServiceOuterClass.Profile toProfileClass() {
        return ProfileServiceOuterClass.Profile.newBuilder()
                .setId(id)
                .setAccountId(accountId)
                .setBio(bio)
                .setName(name)
                .setLocation(location)
                .setWebsite(website)
                .build();
    }
}
