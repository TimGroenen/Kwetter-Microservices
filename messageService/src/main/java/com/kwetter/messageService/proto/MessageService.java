package com.kwetter.messageService.proto;

import com.kwetter.messageService.entities.TweetEntity;
import com.kwetter.messageService.entities.TweetLikedEntity;
import com.kwetter.messageService.kafka.KafkaSender;
import com.kwetter.messageService.kafka.message.KafkaLoggingType;
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
    private final KafkaSender kafkaSender;
    private final TweetRepository tweetRepository;
    private final TweetLikedRepository tweetLikedRepository;

    public MessageService(@Autowired TweetRepository tweetRepository, @Autowired TweetLikedRepository tweetLikedRepository, @Autowired KafkaSender kafkaSender) {
        this.tweetLikedRepository = tweetLikedRepository;
        this.kafkaSender = kafkaSender;
        this.tweetRepository = tweetRepository;
    }

    @Override
    public void createNewTweet(NewTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        SimpleResponse.Builder response = SimpleResponse.newBuilder();

        //Create TweetEntity
        TweetEntity entity = new TweetEntity(request.getProfileId(), request.getContent());
        tweetRepository.save(entity);

        kafkaSender.sendKafkaLogging("New Tweet created for profile: " + request.getProfileId(), KafkaLoggingType.INFO);

        responseObserver.onNext(response.setStatus(true).setMessage("Success").build());
        responseObserver.onCompleted();
    }

    @Override
    public void editTweet(EditTweetRequest request, StreamObserver<TweetResponse> responseObserver) {
        TweetResponse.Builder response = TweetResponse.newBuilder();

        try {
            TweetEntity entity = new TweetEntity(request.getTweet());

            if(tweetRepository.existsById(entity.getId())) {
                kafkaSender.sendKafkaLogging("Tweet with profileId: " + entity.getProfileId() + ", updated", KafkaLoggingType.INFO);
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
            kafkaSender.sendKafkaLogging("Tweet to delete not found", KafkaLoggingType.WARN);
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
                kafkaSender.sendKafkaLogging("Message is already liked by profile", KafkaLoggingType.WARN);
                response.setStatus(false).setMessage("Message is already liked by profile");
            }
        } else {
            kafkaSender.sendKafkaLogging("Tweet not found", KafkaLoggingType.WARN);
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
            kafkaSender.sendKafkaLogging("Tweet is not liked", KafkaLoggingType.WARN);
            response.setStatus(false).setMessage("Tweet is not liked");
        }

        responseObserver.onNext(response.build());
        responseObserver.onCompleted();
    }
}
