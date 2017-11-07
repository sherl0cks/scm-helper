package com.rhc;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.openshift.api.model.Build;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.Project;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;


@RunWith(JUnit4.class)
public class OpenShiftAdapterIT {

    private static final OpenShiftAdapter adapter = new OpenShiftAdapter();
    private static String openShiftProjectName;

    @BeforeClass
    public static void init() {
        int i = ThreadLocalRandom.current().nextInt(0, 1000);
        openShiftProjectName = String.format("scm-helper-integration-test-%d", i);
        Project project = adapter.createProjectIfItDoesNotExist(openShiftProjectName, "SCM Helper Tests");
        Assert.assertNotNull(project);
    }

    @AfterClass
    public static void cleanup() {
        adapter.deleteProject(openShiftProjectName);
    }

    @Test
    public void shouldUpdateBuildConfig() {
        // given
        BuildConfig buildConfig = adapter.createBuildConfigIfItDoesNotExist(openShiftProjectName,"java-app-pipeline", "https://github.com/rht-labs/automation-api", "master", "Jenkinsfile");
        Assert.assertEquals("master", buildConfig.getSpec().getSource().getGit().getRef());


        // when
        BuildConfig result = adapter.updateBuildConfigGitRef(openShiftProjectName, "java-app-pipeline", "https://github.com/sherl0cks/automation-api","dev-demo");


        // then
        Assert.assertNotNull(result);
        Assert.assertEquals("dev-demo", result.getSpec().getSource().getGit().getRef());
        Assert.assertEquals("https://github.com/sherl0cks/automation-api", result.getSpec().getSource().getGit().getUri());

    }

    @Test
    public void shouldTriggerBuildWithNoEnvVars(){
        // given
        BuildConfig buildConfig = adapter.createBuildConfigIfItDoesNotExist(openShiftProjectName,"java-app-pipeline", "https://github.com/rht-labs/automation-api", "master", "Jenkinsfile");

        // when
        Build build = adapter.triggerBuild(openShiftProjectName, "java-app-pipeline");

        // then
        Assert.assertNotNull(build);
        Assert.assertEquals("New", build.getStatus().getPhase() );
        Assert.assertEquals(0, build.getSpec().getStrategy().getJenkinsPipelineStrategy().getEnv().size());
    }


    @Test
    public void shouldTriggerBuildWithEnvVars(){
        // given
        BuildConfig buildConfig = adapter.createBuildConfigIfItDoesNotExist(openShiftProjectName,"java-app-pipeline", "https://github.com/rht-labs/automation-api", "master", "Jenkinsfile");

        Map<String,String> envVars = new HashMap<>();
        envVars.put("foo", "bar");
        envVars.put("calvin", "hobbes");

        // when
        Build build = adapter.triggerBuild(openShiftProjectName, "java-app-pipeline", envVars);

        // then
        Assert.assertNotNull(build);
        Assert.assertEquals("New", build.getStatus().getPhase() );
        List<EnvVar> envVarList = build.getSpec().getStrategy().getJenkinsPipelineStrategy().getEnv();
        Assert.assertEquals(2, envVarList.size());
    }

}
