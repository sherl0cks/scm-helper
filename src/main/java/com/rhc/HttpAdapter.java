package com.rhc;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpAdapter extends AbstractVerticle {

    private static final Logger LOG = LoggerFactory.getLogger(HttpAdapter.class);

    @Override
    public void start(Future<Void> future) {
        Router router = Router.router(vertx);

        // TODO possible we need to update these routes to have parametes in the path e.g. target namespace and buildconfigname
        router.post("/").handler(BodyHandler.create());
        router.post("/").handler(this::loggingHandler);
        router.post("/").handler(this::onPost);
        router.get("/").handler(this::onGet);

        vertx.
                createHttpServer().
                requestHandler(router::accept)
                .listen(
                        // Retrieve the port from the configuration, default to 8080.
                        config().getInteger("http.port", 8080), ar -> {
                            if (ar.succeeded()) {
                                LOG.info("Server starter on port " + ar.result().actualPort());
                            }
                            future.complete();
                        });
    }

    private void onPost(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();

        JsonObject command = GitlabPolicy.translateMergeRequestWebhook(jsonObject);

        vertx.eventBus().send(AddressConstants.UPDATE_BUILD_CONFIG_GIT_REF, command);
        routingContext.response().end();
    }

    private void onGet(RoutingContext routingContext) {
        JsonObject object = new JsonObject();
        object.put("msg", "hello, world! I am the SCM Helper");
        routingContext.response().setStatusCode(200).end(object.toString());
    }

    private void loggingHandler(RoutingContext routingContext) {
        JsonObject jsonObject = routingContext.getBodyAsJson();
        LOG.info(String.format("Incoming request :: Method: %s Path: %s Body: %s", routingContext.request().method(), routingContext.request().path(), jsonObject));
        routingContext.next();
    }

}
