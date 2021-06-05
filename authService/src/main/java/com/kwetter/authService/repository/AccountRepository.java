package com.kwetter.authService.repository;

import com.kwetter.authService.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {
    boolean existsAccountEntityByEmailEquals(String email);
    AccountEntity findAccountEntityByEmail(String email);
}
