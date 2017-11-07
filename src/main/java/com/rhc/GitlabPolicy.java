package com.rhc;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class GitlabPolicy {


    public static JsonObject translateMergeRequestWebhook(JsonObject webhook) {
        JsonObject command = new JsonObject(webhook.toString());
        command.put("triggerBuild", true);

        Map<String,String> envVars = new HashMap<>();
        envVars.put("foo", "bar");
        envVars.put("calvin", "hobbes");
        command.put("envVars", envVars);

        command.put("envVars", envVars);

        return command;
    }
}
