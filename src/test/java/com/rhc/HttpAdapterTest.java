package com.rhc;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import org.junit.*;
import org.junit.runner.RunWith;

import java.util.Map;


@RunWith(VertxUnitRunner.class)
public class HttpAdapterTest {

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

    /**
     *
     * This tests from the http layer. Not sure if this will remain helpful, but if so...
     */
    @Test
    public void shouldRecieveWebhookAndSendTheProperCommandToTheAdapter(TestContext context) {

        // given
        async = context.async();
        JsonObject jsonObject = ObjectMother.mergeRequestWebhook();

        // when
        client.post(8080, "localhost", "/")
            .sendJsonObject(jsonObject, resp -> {
                // then
                context.assertTrue(resp.succeeded());
                context.assertEquals(resp.result().statusCode(), 200);
            });

        this.testContext = context;
    }

    /**
     *
     * .. you'll need to update these assertion
     */
    public void handleEvent(Message message){
        // then
        JsonObject object = ObjectMother.commandFromMergeRequestWebhook();

        Assert.assertEquals(object, message.body());

        async.complete();
    }

}
