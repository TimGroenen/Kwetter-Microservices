package com.kwetter.profileService.repository;

import com.kwetter.profileService.entities.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Long> {
    boolean existsByAccountId(Long id);
    boolean existsById(Long id);
    ProfileEntity getByAccountId(Long id);
    ProfileEntity getById(Long id);
}
