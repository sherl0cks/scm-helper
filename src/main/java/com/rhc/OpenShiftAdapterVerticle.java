package com.rhc;

import io.fabric8.openshift.api.model.BuildConfig;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        JsonObject command = new JsonObject(message.body().toString());

        vertx.executeBlocking(future -> {
                    LOG.debug("in blocking: " + command);
                    BuildConfig result = this.adapter.updateBuildConfigGitRef(
                            command.getString("namespace"),
                            command.getString("buildConfigName"),
                            command.getString("gitUri"),
                            command.getString("gitRef")
                    );
                    if (command.getBoolean("triggerBuild")) {
                        this.adapter.triggerBuild(
                                command.getString("namespace"),
                                command.getString("buildConfigName")
                        );
                    }
                    future.complete(result);
                }, asyncResult -> {
                }
        );

    }

}
