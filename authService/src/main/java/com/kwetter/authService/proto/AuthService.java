package com.kwetter.authService.proto;

import com.kwetter.authService.entity.AccountEntity;
import com.kwetter.authService.repository.AccountRepository;
import io.grpc.stub.StreamObserver;
import com.kwetter.authService.proto.AuthServiceGrpc.AuthServiceImplBase;
import com.kwetter.authService.proto.AuthServiceOuterClass.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class AuthService extends AuthServiceImplBase {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    AccountRepository repository;

    public AuthService(@Autowired AccountRepository repository) {
        this.repository = repository;
    }

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        //Register the account
        RegisterResponse.Builder response = RegisterResponse.newBuilder();
        String email = request.getEmail();
        String password = request.getPassword();

        //Check if email is used
        if(repository.existsAccountEntityByEmailEquals(email)) {
            response.setStatus(false).setMessage("Email in use");
            logger.info("Account creation failed, email used: " + email);
        } else {
            //Save new account
            AccountEntity account = new AccountEntity();
            account.setEmail(email);
            //TODO: Hash password
            account.setPassword(password);
            response.setAccount(repository.save(account).toAccountClass()).setStatus(true).setMessage("Success");
            logger.info("Register request success, email: " + email);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        //Login and return token
        LoginResponse.Builder response = LoginResponse.newBuilder();

        AccountEntity accountToCheck = repository.findAccountEntityByEmail(request.getEmail());
        if(accountToCheck != null && accountToCheck.getPassword().equals(request.getPassword())) {
            //TODO: Generate token
            response.setStatus(true).setMessage("Generated Token");
            logger.info("Successful login, email: " + request.getEmail());
        } else {
            response.setStatus(false).setMessage("Wrong email/password");
            logger.info("failed login, email: " + request.getEmail());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(ValidationRequest request, StreamObserver<ValidationResponse> responseObserver) {
        //Validate token
        ValidationResponse.Builder response = ValidationResponse.newBuilder();

        response.setStatus(true).setMessage("Validation success");

        logger.info("Token validation request success, token: " + request.getToken());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
