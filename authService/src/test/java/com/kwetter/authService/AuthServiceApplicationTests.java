package com.kwetter.authService;

import com.kwetter.authService.entity.AccountEntity;
import com.kwetter.authService.proto.AuthService;
import com.kwetter.authService.proto.AuthServiceOuterClass;
import com.kwetter.authService.proto.AuthServiceOuterClass.LoginRequest;
import com.kwetter.authService.proto.AuthServiceOuterClass.LoginResponse;
import com.kwetter.authService.proto.AuthServiceOuterClass.RegisterResponse;
import com.kwetter.authService.repository.AccountRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthServiceApplicationTests {
    private AuthService service;

    @Mock
    private AccountRepository mockRepo;

    @BeforeEach
    public void setup() {
        service = new AuthService(mockRepo);
    }

    @Test
    public void AuthServiceRegisterTest() {
        String email = "test@test.nl";
        String password = "test";
        AccountEntity account = new AccountEntity();
        account.setId(1L);
        account.setEmail(email);
        account.setPassword(password);
        account.setAdmin(false);

        doReturn(false).when(mockRepo).existsAccountEntityByEmailEquals(email);
        doReturn(account).when(mockRepo).save(any());

        StreamObserver<RegisterResponse> observer = mock(StreamObserver.class);
        service.register(AuthServiceOuterClass.RegisterRequest.newBuilder().setEmail(email).setPassword(password).build(), observer);
        verify(observer, times(1)).onCompleted();
        ArgumentCaptor<RegisterResponse> captor = ArgumentCaptor.forClass(RegisterResponse.class);
        verify(observer, times(1)).onNext(captor.capture());
        RegisterResponse response = captor.getValue();

        assertTrue(response.getStatus());
    }

    @Test
    public void AuthServiceRegisterTestEmailUsed() {
        String email = "test@test.nl";
        String password = "test";

        doReturn(true).when(mockRepo).existsAccountEntityByEmailEquals(email);

        StreamObserver<RegisterResponse> observer = mock(StreamObserver.class);
        service.register(AuthServiceOuterClass.RegisterRequest.newBuilder().setEmail(email).setPassword(password).build(), observer);
        verify(observer, times(1)).onCompleted();
        ArgumentCaptor<RegisterResponse> captor = ArgumentCaptor.forClass(RegisterResponse.class);
        verify(observer, times(1)).onNext(captor.capture());
        RegisterResponse response = captor.getValue();

        assertFalse(response.getStatus());
    }

    @Test
    public void AuthServiceLoginTest() {
        String email = "test@test.nl";
        String password = "test";
        String encodedPassword = "$2a$10$VJ.AZ3i/D3ZYCR4j2scmc.RPOU0Y9c.i7DDN9CdcNf9tNTH4F587i";
        AccountEntity accountToCheck = new AccountEntity();
        accountToCheck.setId(1L);
        accountToCheck.setEmail(email);
        accountToCheck.setPassword(encodedPassword);
        accountToCheck.setAdmin(false);

        doReturn(accountToCheck).when(mockRepo).findAccountEntityByEmail(email);

        StreamObserver<LoginResponse> observer = mock(StreamObserver.class);
        service.login(LoginRequest.newBuilder().setEmail(email).setPassword(password).build(), observer);
        verify(observer, times(1)).onCompleted();
        ArgumentCaptor<LoginResponse> captor = ArgumentCaptor.forClass(LoginResponse.class);
        verify(observer, times(1)).onNext(captor.capture());
        LoginResponse response = captor.getValue();

        assertTrue(response.getStatus());
    }

    @Test
    public void AuthServiceLoginFailedTest() {
        String email = "test@test.nl";
        String password = "test";

        doReturn(null).when(mockRepo).findAccountEntityByEmail(email);

        StreamObserver<LoginResponse> observer = mock(StreamObserver.class);
        service.login(LoginRequest.newBuilder().setEmail(email).setPassword(password).build(), observer);
        verify(observer, times(1)).onCompleted();
        ArgumentCaptor<LoginResponse> captor = ArgumentCaptor.forClass(LoginResponse.class);
        verify(observer, times(1)).onNext(captor.capture());
        LoginResponse response = captor.getValue();

        assertFalse(response.getStatus());
    }
}
