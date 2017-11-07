package com.rhc;

import io.fabric8.openshift.api.model.Build;
import io.fabric8.openshift.api.model.BuildConfig;
import io.fabric8.openshift.api.model.Project;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This test requires an openshift login on the local machine, or the following system properties / env vars set:
 * <p>
 * kubernetes.master / KUBERNETES_MASTER
 * <p>
 * and for auth, either of the combinations below:
 * <p>
 * kubernetes.auth.basic.username / KUBERNETES_AUTH_BASIC_USERNAME
 * kubernetes.auth.basic.password / KUBERNETES_AUTH_BASIC_PASSWORD
 * <p>
 * or
 * <p>
 * kubernetes.auth.token / KUBERNETES_AUTH_TOKEN
 * <p>
 * <p>
 * <p>
 * for more info see https://github.com/fabric8io/kubernetes-client
 */
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
        BuildConfig result = adapter.updateBuildConfigGitRef(openShiftProjectName, "java-app-pipeline", "dev-demo");


        // then
        Assert.assertNotNull(result);
        Assert.assertEquals("dev-demo", result.getSpec().getSource().getGit().getRef());

    }

    @Test
    public void shouldTriggerBuild(){
        // given
        BuildConfig buildConfig = adapter.createBuildConfigIfItDoesNotExist(openShiftProjectName,"java-app-pipeline", "https://github.com/rht-labs/automation-api", "master", "Jenkinsfile");
        Assert.assertEquals("master", buildConfig.getSpec().getSource().getGit().getRef());

        // when
        Build build = adapter.triggerBuild(openShiftProjectName, "java-app-pipeline");

        // then
        Assert.assertNotNull(build);
        Assert.assertEquals("New", build.getStatus().getPhase() );
    }

}
