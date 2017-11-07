package com.rhc;

import io.vertx.core.json.JsonObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GitlabPolicyTest {

    /**
     * enhance this unit test as you build out the translation
     */
    @Test
    public void shouldProperlyTranslateWebhookToCommand(){
        // given
        JsonObject webhook = ObjectMother.mergeRequestWebhook();

        // when
        JsonObject command = GitlabPolicy.translateMergeRequestWebhook(webhook);

        // then
        webhook.put("triggerBuild", true);
        Assert.assertEquals(ObjectMother.commandFromMergeRequestWebhook(), command);
    }
}
