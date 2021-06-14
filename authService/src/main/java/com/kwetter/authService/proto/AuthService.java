package com.kwetter.authService.proto;

import com.kwetter.authService.entity.AccountEntity;
import com.kwetter.authService.kafka.KafkaSender;
import com.kwetter.authService.kafka.message.KafkaLoggingType;
import com.kwetter.authService.repository.AccountRepository;
import com.kwetter.authService.security.JwtTokenUtil;
import io.grpc.stub.StreamObserver;
import com.kwetter.authService.proto.AuthServiceGrpc.AuthServiceImplBase;
import com.kwetter.authService.proto.AuthServiceOuterClass.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@GrpcService
public class AuthService extends AuthServiceImplBase {
    private final KafkaSender kafkaSender;
    private final AccountRepository repository;

    public AuthService(@Autowired AccountRepository repository, @Autowired KafkaSender kafkaSender) {
        this.kafkaSender = kafkaSender;
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
            kafkaSender.sendKafkaLogging("Account creation failed, email used: " + email, KafkaLoggingType.WARN);
        } else {
            //Save new account
            AccountEntity account = new AccountEntity();
            account.setEmail(email);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, new SecureRandom());
            account.setPassword(encoder.encode(password));
            response.setAccount(repository.save(account).toAccountClass()).setStatus(true).setMessage("Success");
            kafkaSender.sendKafkaLogging("Account creation success, email used: " + email, KafkaLoggingType.INFO);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void login(LoginRequest request, StreamObserver<LoginResponse> responseObserver) {
        //Login and return token
        LoginResponse.Builder response = LoginResponse.newBuilder();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10, new SecureRandom());
        AccountEntity accountToCheck = repository.findAccountEntityByEmail(request.getEmail());
        if(accountToCheck != null && encoder.matches(request.getPassword(), accountToCheck.getPassword())) {
            String token = new JwtTokenUtil().generateToken(request.getEmail());
            response.setStatus(true).setMessage(token);
            kafkaSender.sendKafkaLogging("Successful login, email: " + request.getEmail(), KafkaLoggingType.INFO);
        } else {
            response.setStatus(false).setMessage("Wrong email/password");
            kafkaSender.sendKafkaLogging("failed login, email: " + request.getEmail(), KafkaLoggingType.WARN);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(ValidationRequest request, StreamObserver<ValidationResponse> responseObserver) {
        //Validate token
        ValidationResponse.Builder response = ValidationResponse.newBuilder();

        if(new JwtTokenUtil().validateToken(request.getToken())) {
            response.setStatus(true).setMessage("Valid token");
            kafkaSender.sendKafkaLogging("Valid token", KafkaLoggingType.WARN);
        } else {
            response.setStatus(false).setMessage("Invalid token");
            kafkaSender.sendKafkaLogging("Invalid token", KafkaLoggingType.WARN);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getAccountByEmail(EmailRequest request, StreamObserver<RegisterResponse> responseObserver) {
        RegisterResponse.Builder response = RegisterResponse.newBuilder();

        AccountEntity entity = repository.findAccountEntityByEmail(request.getEmail());

        if(entity != null) {
            response.setStatus(true).setMessage("Success").setAccount(entity.toAccountClass());
            kafkaSender.sendKafkaLogging("Found account with email: " + request.getEmail(), KafkaLoggingType.INFO);
        } else {
            response.setStatus(false).setMessage("Account not found");
            kafkaSender.sendKafkaLogging("Account not found with email: " + request.getEmail(), KafkaLoggingType.WARN);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
