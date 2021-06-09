package com.kwetter.profileService.proto;

import com.kwetter.profileService.entities.FollowUserEntity;
import com.kwetter.profileService.entities.ProfileEntity;
import com.kwetter.profileService.proto.ProfileServiceGrpc.*;
import com.kwetter.profileService.repository.FollowUserRepository;
import com.kwetter.profileService.repository.ProfileRepository;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
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
            logger.info("New profile with name: " + profileEntity.getName() + ", created");
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
                logger.info("Profile with name: " + entity.getName() + ", updated");
                response.setStatus(true).setMessage("Success").setProfile(profileRepository.save(entity).toProfileClass());
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
    public void getProfileById(GetByProfileIdRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileResponse.Builder response = ProfileResponse.newBuilder();

        ProfileEntity entity = profileRepository.getById(request.getProfileId());

        if(entity == null) {
            logger.info("Profile not found");
            response.setStatus(false).setMessage("Profile not found");
        } else {
            logger.info("Found profile with name: " + entity.getName());
            response.setProfile(entity.toProfileClass()).setStatus(true).setMessage("Success");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getProfileByUserId(GetProfileByUserIdRequest request, StreamObserver<ProfileResponse> responseObserver) {
        ProfileResponse.Builder response = ProfileResponse.newBuilder();

        ProfileEntity entity = profileRepository.getByAccountId(request.getUserId());

        if(entity == null) {
            logger.info("Profile not found");
            response.setStatus(false).setMessage("Profile not found");
        } else {
            logger.info("Found profile with name: " + entity.getName());
            response.setProfile(entity.toProfileClass()).setStatus(true).setMessage("Success");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void followUser(FollowUserRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Check if both profiles exist
        if(profileRepository.existsById(request.getUserFollowingId()) && profileRepository.existsById(request.getUserFollowedId())) {
            //Check if follow already exists
            if(!followUserRepository.existsByUserFollowingIdAndUserFollowedId(request.getUserFollowingId(), request.getUserFollowedId())) {
                //Both profiles exist, add follow line to database
                followUserRepository.save(
                        new FollowUserEntity(
                                profileRepository.getById(request.getUserFollowingId()),
                                profileRepository.getById(request.getUserFollowedId())
                        )
                );
                response.setStatus(true).setMessage("Success");
            } else {
                //Follow row already exists
                logger.info("User is already followed");
                response.setStatus(false).setMessage("User is already followed");
            }
        } else {
            logger.info("Profile not found");
            response.setStatus(false).setMessage("Profile does not exist");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void unfollowUser(FollowUserRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Try to find row
        FollowUserEntity result = followUserRepository.getByUserFollowingIdAndUserFollowedId(request.getUserFollowingId(), request.getUserFollowedId());

        if (result != null) {
            //Delete the row
            followUserRepository.delete(result);
            response.setStatus(true).setMessage("Success");
        } else {
            //User is not followed
            logger.info("User is not followed");
            response.setStatus(false).setMessage("User is not followed");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFollowed(GetByProfileIdRequest request, StreamObserver<ProfilesResponse> responseObserver) {
        ProfilesResponse.Builder response = ProfilesResponse.newBuilder();

        ProfileEntity profile = profileRepository.getById(request.getProfileId());

        for (FollowUserEntity f: profile.getFollowing()) {
            response.addProfiles(f.getUserFollowed().toProfileClass());
        }

        logger.info("Returning followed for ProfileId: " + request.getProfileId());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getFollowers(GetByProfileIdRequest request, StreamObserver<ProfilesResponse> responseObserver) {
        ProfilesResponse.Builder response = ProfilesResponse.newBuilder();

        ProfileEntity profile = profileRepository.getById(request.getProfileId());

        for (FollowUserEntity f: profile.getFollowed()) {
            response.addProfiles(f.getUserFollowing().toProfileClass());
        }

        logger.info("Returning followers for ProfileId: " + request.getProfileId());
        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
