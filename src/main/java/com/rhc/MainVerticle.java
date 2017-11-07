package com.rhc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

public class MainVerticle extends AbstractVerticle {

    @Override
    public void start(Future<Void> future) {
        vertx.deployVerticle(OpenShiftAdapterVerticle.class.getName());
        vertx.deployVerticle(HttpAdapter.class.getName());
    }
}
