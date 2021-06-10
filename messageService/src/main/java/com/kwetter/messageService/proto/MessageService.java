package com.kwetter.messageService.proto;

import com.kwetter.messageService.entities.TweetEntity;
import com.kwetter.messageService.entities.TweetLikedEntity;
import com.kwetter.messageService.repositories.TweetLikedRepository;
import com.kwetter.messageService.repositories.TweetRepository;
import io.grpc.stub.StreamObserver;
import com.kwetter.messageService.proto.MessageServiceOuterClass.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@GrpcService
public class MessageService extends MessageServiceGrpc.MessageServiceImplBase {
    private final Logger logger = LoggerFactory.getLogger(MessageService.class);

    private TweetRepository tweetRepository;
    private TweetLikedRepository tweetLikedRepository;

    public MessageService(@Autowired TweetRepository tweetRepository, @Autowired TweetLikedRepository tweetLikedRepository) {
        this.tweetLikedRepository = tweetLikedRepository;
        this.tweetRepository = tweetRepository;
    }

    @Override
    public void createNewTweet(NewTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Create TweetEntity
        TweetEntity entity = new TweetEntity(request.getProfileId(), request.getContent());
        tweetRepository.save(entity);

        logger.info("New Tweet created for profile: " + request.getProfileId());

        responseObserver.onNext(response.setStatus(true).setMessage("Success").build());
        responseObserver.onCompleted();
    }

    @Override
    public void editTweet(EditTweetRequest request, StreamObserver<TweetResponse> responseObserver) {
        TweetResponse.Builder response = TweetResponse.newBuilder();

        try {
            TweetEntity entity = new TweetEntity(request.getTweet());

            if(tweetRepository.existsById(entity.getId())) {
                logger.info("Tweet with profileId: " + entity.getProfileId() + ", updated");
                response.setStatus(true).setMessage("Success").setTweet(tweetRepository.save(entity).toTweetClass());
            } else {
                response.setStatus(false).setMessage("Tweet with id does not exist");
            }
        } catch (Exception e) {
            response.setStatus(false).setMessage(e.getMessage());
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void deleteTweet(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        TweetEntity entity = tweetRepository.getById(request.getTweetId());

        if(entity != null) {
            tweetRepository.delete(entity);
            response.setStatus(true).setMessage("Success");
        } else {
            logger.info("Tweet to delete not found");
            response.setMessage("Not found").setStatus(false);
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void getTweetsByProfileIds(TweetsByProfileRequest request, StreamObserver<TweetsResponse> responseObserver) {
        TweetsResponse.Builder response = TweetsResponse.newBuilder();

        List<TweetEntity> entityList = tweetRepository.getByProfileIds(request.getProfileIdsList());

        //Create response list
        List<Tweet> tweets = new ArrayList<>();
        for (TweetEntity t: entityList) {
            boolean isLiked = tweetLikedRepository.existsByProfileIdAndTweetId(request.getProfileId(), t.getId());
            int likes = tweetLikedRepository.countByTweetId(t.getId());
            tweets.add(t.toTweetClass(isLiked, likes));
        }

        response.addAllTweets(tweets);

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void like(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Check if tweet exist
        if(tweetRepository.existsById(request.getTweetId())) {
            //Check if like already exists
            if(!tweetLikedRepository.existsByProfileIdAndTweetId(request.getProfileId(), request.getTweetId())) {
                //add like line to database
                tweetLikedRepository.save(new TweetLikedEntity(request.getProfileId(), tweetRepository.getById(request.getTweetId())));
                response.setStatus(true).setMessage("Success");
            } else {
                //Like row already exists
                logger.info("Message is already liked by profile");
                response.setStatus(false).setMessage("Message is already liked by profile");
            }
        } else {
            logger.info("Tweet not found");
            response.setStatus(false).setMessage("Tweet does not exist");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }

    @Override
    public void unlike(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Try to find row
        TweetLikedEntity result = tweetLikedRepository.getByProfileIdAndTweetId(request.getProfileId(), request.getTweetId());

        if (result != null) {
            //Delete the row
            tweetLikedRepository.delete(result);
            response.setStatus(true).setMessage("Success");
        } else {
            //User is not followed
            logger.info("Tweet is not liked");
            response.setStatus(false).setMessage("Tweet is not liked");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
