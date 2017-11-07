package com.rhc;

import io.fabric8.openshift.api.model.BuildConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenShiftAdapterVerticle extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftAdapterVerticle.class);

    private OpenShiftAdapter adapter;

    @Override
    public void start(Future<Void> future) {

        this.adapter = new OpenShiftAdapter();

        vertx.eventBus().consumer(AddressConstants.UPDATE_BUILD_CONFIG_GIT_REF).handler(this::updateBuildConfigGitRef);

        future.complete();
    }

    public void updateBuildConfigGitRef(Message message) {
        LOG.debug("receive");
        JsonObject command = (JsonObject) message.body();

        String namespace = command.getString("namespace");
        String buildConfigName = command.getString("buildConfigName");
        String gitRef = command.getString("gitRef");
        boolean triggerBuild = command.getBoolean("triggerBuild");

        vertx.executeBlocking(future -> {
            LOG.debug("in blocking");
                    BuildConfig result = this.adapter.updateBuildConfigGitRef(namespace, buildConfigName, gitRef);
                    if (triggerBuild) {
                        this.adapter.triggerBuild(namespace, buildConfigName);
                    }
                    future.complete(result);
                }, asyncResult -> {
                }
        );

    }

}
