package com.kwetter.profileService.proto;

import com.kwetter.profileService.entities.ProfileEntity;
import com.kwetter.profileService.proto.ProfileServiceGrpc.*;
import com.kwetter.profileService.repository.FollowUserRepository;
import com.kwetter.profileService.repository.ProfileRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.hibernate.cfg.NotYetImplementedException;
import com.kwetter.profileService.proto.ProfileServiceOuterClass.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class ProfileService extends ProfileServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(ProfileService.class);

    private ProfileRepository profileRepository;
    private FollowUserRepository followUserRepository;

    public ProfileService(@Autowired ProfileRepository profileRepository, @Autowired FollowUserRepository followUserRepository) {
        this.profileRepository = profileRepository;
        this.followUserRepository = followUserRepository;
    }

    @Override
    public void createNewProfile(NewProfileRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileResponse.Builder response = ProfileResponse.newBuilder();

        //Check if a profile with accountId exists
        if(profileRepository.existsByAccountId(request.getAccountId())) {
            response.setStatus(false).setMessage("Profile with accountId exists");
        } else {
            ProfileEntity profileEntity = new ProfileEntity();
            profileEntity.setAccountId(request.getAccountId());
            profileEntity.setName(request.getName());
            profileEntity.setBio("");
            profileEntity.setLocation("");
            profileEntity.setWebsite("");
            response.setProfile(profileRepository.save(profileEntity).toProfileClass()).setStatus(true).setMessage("Success");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void updateProfileInfo(UpdateProfileInfoRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileResponse.Builder response = ProfileResponse.newBuilder();

        try {
            ProfileEntity entity = new ProfileEntity(request.getProfile());

            if(profileRepository.existsById(entity.getId())) {
                response.setProfile(profileRepository.save(entity).toProfileClass());
            } else {
                response.setStatus(false).setMessage("Profile with id does not exist");
            }
        } catch (Exception e) {
            response.setStatus(false).setMessage(e.getMessage());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProfileByUserId(GetProfileByUserIdRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileResponse.Builder response = ProfileResponse.newBuilder();

        ProfileEntity entity = profileRepository.getByAccountId(request.getUserId());

        if(entity == null) {
            response.setStatus(false).setMessage("Profile not found");
        } else {
            response.setProfile(entity.toProfileClass()).setStatus(true).setMessage("Success");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
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
