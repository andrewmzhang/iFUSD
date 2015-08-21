package com.fremontunified.ifusd.app;


public interface FeedConsumer {
    void setFeed(Feed feed);

    void handleError(String message);
}
