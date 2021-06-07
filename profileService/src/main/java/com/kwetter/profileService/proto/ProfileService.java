package com.kwetter.profileService.proto;

import com.kwetter.profileService.proto.ProfileServiceGrpc.*;
import io.grpc.stub.StreamObserver;
import org.hibernate.cfg.NotYetImplementedException;
import com.kwetter.profileService.proto.ProfileServiceOuterClass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileService extends ProfileServiceImplBase {
    Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Override
    public void createNewProfile(NewProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void updateProfileInfo(UpdateProfileInfoRequest request, StreamObserver<ProfileResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void getProfileByUserId(GetProfileByUserIdRequest request, StreamObserver<ProfileResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void followUser(FollowUserRequest request, StreamObserver<SimpleResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void unfollowUser(FollowUserRequest request, StreamObserver<SimpleResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void getFollowed(GetByProfileIdRequest request, StreamObserver<ProfilesResponse> responseObserver) {
        throw new NotYetImplementedException();
    }

    @Override
    public void getFollowers(GetByProfileIdRequest request, StreamObserver<ProfilesResponse> responseObserver) {
        throw new NotYetImplementedException();
    }
}
