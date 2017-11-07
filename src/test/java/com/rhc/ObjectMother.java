package com.rhc;

import io.vertx.core.json.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class ObjectMother {

    public static JsonObject mergeRequestWebhook(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("namespace", "labs-ci-cd");
        jsonObject.put("buildConfigName", "java-app-pipeline");
        jsonObject.put("gitUri","https://github.com/rht-labs/automation-api");
        jsonObject.put("gitRef","dev-demo");
        return  jsonObject;
    }

    public static JsonObject commandFromMergeRequestWebhook(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("namespace", "labs-ci-cd");
        jsonObject.put("buildConfigName", "java-app-pipeline");
        jsonObject.put("gitUri","https://github.com/rht-labs/automation-api");
        jsonObject.put("gitRef","dev-demo");
        jsonObject.put("triggerBuild", true);

        Map<String,String> envVars = new HashMap<>();
        envVars.put("foo", "bar");
        envVars.put("calvin", "hobbes");
        jsonObject.put("envVars", envVars);

        return  jsonObject;
    }
}
