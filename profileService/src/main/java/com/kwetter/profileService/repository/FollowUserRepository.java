package com.kwetter.profileService.repository;

import com.kwetter.profileService.entities.FollowUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowUserRepository extends JpaRepository<FollowUserEntity, Long> {

}
