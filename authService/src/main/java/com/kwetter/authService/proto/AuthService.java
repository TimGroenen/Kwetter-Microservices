package com.kwetter.authService.proto;

import io.grpc.stub.StreamObserver;
import com.kwetter.authService.proto.AuthServiceGrpc.AuthServiceImplBase;
import com.kwetter.authService.proto.AuthServiceOuterClass.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@GrpcService
public class AuthService extends AuthServiceImplBase {
    Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Override
    public void register(RegisterRequest request, StreamObserver<RegisterResponse> responseObserver) {
        //Register the account
        RegisterResponse.Builder response = RegisterResponse.newBuilder();

        String email = request.getEmail();
        String password = request.getPassword();

        response.setAccount(Account.newBuilder().setId(1).setProfileId(1).setRole(1).setEmail(email).setPassword(password).build()).setStatus(true).setMessage("Register success");

        logger.info("Register request success, email: " + email);
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        //Login and return token
        LoginResponse.Builder response = LoginResponse.newBuilder();
        response.setStatus(true).setMessage("Success");

        logger.info("Login request success, email: " + request.getEmail());
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
