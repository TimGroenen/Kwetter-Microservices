package com.kwetter.profileService;

import com.kwetter.profileService.entities.FollowUserEntity;
import com.kwetter.profileService.entities.ProfileEntity;
import com.kwetter.profileService.proto.ProfileService;
import com.kwetter.profileService.proto.ProfileServiceOuterClass.*;
import com.kwetter.profileService.repository.FollowUserRepository;
import com.kwetter.profileService.repository.ProfileRepository;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class ProfileServiceApplicationTests {
	private ProfileService service;

	@Mock
	private ProfileRepository profileRepo;

	@Mock
	private FollowUserRepository followRepo;

	private ProfileEntity entity;
	private FollowUserEntity followEntity;

	@BeforeEach
	void setUp() {
		service = new ProfileService(profileRepo, followRepo);
		entity = new ProfileEntity();
		entity.setId(1L);
		entity.setAccountId(1L);
		entity.setName("Test");
		entity.setBio("");
		entity.setLocation("");
		entity.setWebsite("");
		followEntity = new FollowUserEntity(entity, entity);
	}

	@Test
	void createNewProfileTest() {
		NewProfileRequest request = NewProfileRequest.newBuilder().setName("Test").setAccountId(1L).build();

		doReturn(false).when(profileRepo).existsByAccountId(request.getAccountId());
		doReturn(entity).when(profileRepo).save(any());

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.createNewProfile(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertTrue(response.getStatus());
	}

	@Test
	void createNewProfileWrongIdTest() {
		NewProfileRequest request = NewProfileRequest.newBuilder().setName("Test").setAccountId(1L).build();

		doReturn(true).when(profileRepo).existsByAccountId(request.getAccountId());

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.createNewProfile(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void updateProfileInfoTest() {
		Profile profile = Profile.newBuilder().setId(1L).setAccountId(1L).setName("Test").setWebsite("").setLocation("").setBio("").build();
		UpdateProfileInfoRequest request = UpdateProfileInfoRequest.newBuilder().setProfile(profile).build();

		doReturn(true).when(profileRepo).existsById(any());
		doReturn(entity).when(profileRepo).save(any());

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.updateProfileInfo(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertTrue(response.getStatus());
	}

	@Test
	void updateProfileInfoNotFoundTest() {
		Profile profile = Profile.newBuilder().setId(1L).setAccountId(1L).setName("Test").setWebsite("").setLocation("").setBio("").build();
		UpdateProfileInfoRequest request = UpdateProfileInfoRequest.newBuilder().setProfile(profile).build();

		doReturn(false).when(profileRepo).existsById(any());

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.updateProfileInfo(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void getProfileByUserIdTest() {
		GetProfileByUserIdRequest request = GetProfileByUserIdRequest.newBuilder().setUserId(1L).build();

		doReturn(entity).when(profileRepo).getByAccountId(1L);

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.getProfileByUserId(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertTrue(response.getStatus());
	}

	@Test
	void getProfileByUserIdNotFoundTest() {
		GetProfileByUserIdRequest request = GetProfileByUserIdRequest.newBuilder().setUserId(1L).build();

		doReturn(null).when(profileRepo).getByAccountId(1L);

		StreamObserver<ProfileResponse> observer = mock(StreamObserver.class);
		service.getProfileByUserId(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfileResponse> captor = ArgumentCaptor.forClass(ProfileResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfileResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void followUserTest() {
		FollowUserRequest request = FollowUserRequest.newBuilder().setUserFollowedId(1).setUserFollowingId(2).build();

		doReturn(true).when(profileRepo).existsById(any());
		doReturn(false).when(followRepo).existsByUserFollowingIdAndUserFollowedId(any(), any());
		doReturn(followEntity).when(followRepo).save(any());

		StreamObserver<SimpleResponse> observer = mock(StreamObserver.class);
		service.followUser(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<SimpleResponse> captor = ArgumentCaptor.forClass(SimpleResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		SimpleResponse response = captor.getValue();

		assertTrue(response.getStatus());
	}

	@Test
	void followUserNotFoundTest() {
		FollowUserRequest request = FollowUserRequest.newBuilder().setUserFollowedId(1).setUserFollowingId(2).build();

		doReturn(false).when(profileRepo).existsById(any());

		StreamObserver<SimpleResponse> observer = mock(StreamObserver.class);
		service.followUser(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<SimpleResponse> captor = ArgumentCaptor.forClass(SimpleResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		SimpleResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void followUserAlreadyExistsTest() {
		FollowUserRequest request = FollowUserRequest.newBuilder().setUserFollowedId(1).setUserFollowingId(2).build();

		doReturn(true).when(profileRepo).existsById(any());
		doReturn(true).when(followRepo).existsByUserFollowingIdAndUserFollowedId(any(), any());

		StreamObserver<SimpleResponse> observer = mock(StreamObserver.class);
		service.followUser(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<SimpleResponse> captor = ArgumentCaptor.forClass(SimpleResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		SimpleResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void unfollowUserTest() {
		FollowUserRequest request = FollowUserRequest.newBuilder().setUserFollowedId(1).setUserFollowingId(2).build();

		doReturn(followEntity).when(followRepo).getByUserFollowingIdAndUserFollowedId(any(), any());

		StreamObserver<SimpleResponse> observer = mock(StreamObserver.class);
		service.unfollowUser(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<SimpleResponse> captor = ArgumentCaptor.forClass(SimpleResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		SimpleResponse response = captor.getValue();

		assertTrue(response.getStatus());
	}

	@Test
	void unfollowUserNotFollowedTest() {
		FollowUserRequest request = FollowUserRequest.newBuilder().setUserFollowedId(1).setUserFollowingId(2).build();

		doReturn(null).when(followRepo).getByUserFollowingIdAndUserFollowedId(any(), any());

		StreamObserver<SimpleResponse> observer = mock(StreamObserver.class);
		service.unfollowUser(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<SimpleResponse> captor = ArgumentCaptor.forClass(SimpleResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		SimpleResponse response = captor.getValue();

		assertFalse(response.getStatus());
	}

	@Test
	void getFollowedTest() {
		GetByProfileIdRequest request = GetByProfileIdRequest.newBuilder().setProfileId(1L).build();
		List<FollowUserEntity> follows = new ArrayList<>();
		follows.add(followEntity);
		follows.add(followEntity);
		follows.add(followEntity);

		entity.setFollowing(follows);

		doReturn(entity).when(profileRepo).getById(any());

		StreamObserver<ProfilesResponse> observer = mock(StreamObserver.class);
		service.getFollowed(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfilesResponse> captor = ArgumentCaptor.forClass(ProfilesResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfilesResponse response = captor.getValue();

		assertEquals(3, response.getProfilesList().size());
	}

	@Test
	void getFollowersTest() {
		GetByProfileIdRequest request = GetByProfileIdRequest.newBuilder().setProfileId(1L).build();
		List<FollowUserEntity> follows = new ArrayList<>();
		follows.add(followEntity);
		follows.add(followEntity);
		follows.add(followEntity);

		entity.setFollowed(follows);

		doReturn(entity).when(profileRepo).getById(any());

		StreamObserver<ProfilesResponse> observer = mock(StreamObserver.class);
		service.getFollowers(request, observer);
		verify(observer, times(1)).onCompleted();
		ArgumentCaptor<ProfilesResponse> captor = ArgumentCaptor.forClass(ProfilesResponse.class);
		verify(observer, times(1)).onNext(captor.capture());
		ProfilesResponse response = captor.getValue();

		assertEquals(3, response.getProfilesList().size());
	}


}
