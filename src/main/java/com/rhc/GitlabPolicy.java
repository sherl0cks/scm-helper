package com.rhc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitlabPolicy extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(GitlabPolicy.class);

    @Override
    public void start(Future<Void> future) {
        future.complete();
    }
}
