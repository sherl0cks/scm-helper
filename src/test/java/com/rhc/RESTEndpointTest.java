package com.rhc;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class RESTEndpointTest {

    private Vertx vertx;
    private WebClient client;
    private TestContext testContext;
    private Async async;

    @Before
    public void before(TestContext context) {
        vertx = Vertx.vertx();
        vertx.exceptionHandler(context.exceptionHandler());
        vertx.deployVerticle(HttpAdapter.class.getName(), context.asyncAssertSuccess());
        client = WebClient.create(vertx);

        vertx.eventBus().consumer(AddressConstants.UPDATE_BUILD_CONFIG_GIT_REF).handler(this::handleEvent);
    }

    @After
    public void after(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void shouldReceivePost(TestContext context) {

        // given
        async = context.async();
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("foo", "bar");

        // when
        client.post(8080, "localhost", "/")
            .sendJsonObject(jsonObject, resp -> {
                // then
                context.assertTrue(resp.succeeded());
                context.assertEquals(resp.result().statusCode(), 200);
            });

        this.testContext = context;
    }

    public void handleEvent(Message message){
        // then
        JsonObject object = new JsonObject();
        object.put("foo", "bar");
        testContext.assertEquals(object, message.body());

        async.complete();
    }

}
