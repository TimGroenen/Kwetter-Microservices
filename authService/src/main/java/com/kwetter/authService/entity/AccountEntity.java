package com.kwetter.authService.entity;

import com.kwetter.authService.proto.AuthServiceOuterClass;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "Account")
@Getter
@Setter
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String email;
    String password;
    boolean isAdmin;

    public AccountEntity() {}

    public AccountEntity(AuthServiceOuterClass.Account account) {
        this.id = account.getId();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.isAdmin = account.getIsAdmin();
    }

    public AuthServiceOuterClass.Account toAccountClass() {
        return AuthServiceOuterClass.Account.newBuilder()
                .setId(id)
                .setEmail(email)
                .setPassword(password)
                .setIsAdmin(isAdmin)
                .build();
    }
}
