package com.kwetter.messageService.proto;

import com.kwetter.messageService.repositories.TweetLikedRepository;
import com.kwetter.messageService.repositories.TweetRepository;
import io.grpc.stub.StreamObserver;
import com.kwetter.messageService.proto.MessageServiceOuterClass.*;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

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
        super.createNewTweet(request, responseObserver);
    }

    @Override
    public void editTweet(EditTweetRequest request, StreamObserver<TweetResponse> responseObserver) {
        super.editTweet(request, responseObserver);
    }

    @Override
    public void deleteTweet(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        super.deleteTweet(request, responseObserver);
    }

    @Override
    public void getTweetsByProfileIds(TweetsByProfileRequest request, StreamObserver<TweetsResponse> responseObserver) {
        super.getTweetsByProfileIds(request, responseObserver);
    }

    @Override
    public void like(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        super.like(request, responseObserver);
    }

    @Override
    public void unlike(LikeTweetRequest request, StreamObserver<SimpleResponse> responseObserver) {
        super.unlike(request, responseObserver);
    }
}
