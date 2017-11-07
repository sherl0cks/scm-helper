package com.rhc;

import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.openshift.api.model.*;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenShiftAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(OpenShiftAdapter.class);

    private OpenShiftClient openShiftClient;

    public OpenShiftAdapter() {
        this.openShiftClient = new DefaultOpenShiftClient();
    }

    public BuildConfig createBuildConfigIfItDoesNotExist(String namespace, String name, String gitUri, String gitRef, String jenkinsfilePath) {

        BuildConfig buildConfig = new BuildConfigBuilder()
                .withNewSpec()
                .withNewSource().withNewGit().withUri(gitUri).withRef(gitRef).endGit().endSource()
                .withNewStrategy().withNewJenkinsPipelineStrategy().withJenkinsfilePath(jenkinsfilePath).endJenkinsPipelineStrategy().endStrategy()
                .withTriggers().addNewTrigger().withType("ConfigChange").endTrigger()
                .endSpec()
                .withNewMetadata().withName(name)
                .endMetadata()
                .build();

        try {
            BuildConfig response = openShiftClient.buildConfigs().inNamespace(namespace).create(buildConfig);
            return response;
        } catch (KubernetesClientException e) {
            if (e.getMessage().contains("already exists")) {
                LOG.info(String.format("BuildConfig %s already exists", name));
                return openShiftClient.buildConfigs().inNamespace(namespace).withName(name).get();
            } else {
                throw e;
            }
        }

    }

    public BuildConfig updateBuildConfigGitRef(String namespace, String buildConfigName, String gitUri, String gitRef) {
        try {

            BuildConfig result = openShiftClient.buildConfigs().inNamespace(namespace).withName(buildConfigName).cascading(false).edit()
                    .editSpec()
                    .editSource().withNewGit().withUri(gitUri).withRef(gitRef).endGit().endSource()
                    .endSpec()
                    .done();
            LOG.info(String.format("BuildConfig %s patched with gitRef %s", buildConfigName, result.getSpec().getSource().getGit().getRef()));
            return result;
        } catch (KubernetesClientException e) {

            if (e.getMessage().contains("not found")) {
                LOG.info(String.format("BuildConfig %s not found", buildConfigName));
                return null;
            } else {
                LOG.error("Failed to update build config");
                LOG.error(e.getMessage());
                return null;
            }
        }
    }

    public Build triggerBuild(String namespace, String buildConfigName) {
        return triggerBuild(namespace, buildConfigName, null);
    }
    public Build triggerBuild(String namespace, String buildConfigName, Map<String,String> envVars) {
        BuildRequest buildRequest = new BuildRequestBuilder()
                .withNewMetadata().withName(buildConfigName)
                .endMetadata()
                .withEnv(buildEnvVarList(envVars))
                .build();
        Build build = openShiftClient.buildConfigs().inNamespace(namespace).withName(buildConfigName).instantiate(buildRequest);
        LOG.info(String.format("Build %s instantiated", build.getMetadata().getName()));
        return build;
    }

    public Project createProjectIfItDoesNotExist(String name, String displayName) {

        Map<String, String> annotations = new HashMap<>();
        annotations.put("openshift.io/display-name", displayName);
        Project newProject = new ProjectBuilder()
                .withNewMetadata().withName(name).withAnnotations(annotations).endMetadata()
                .build();

        try {
            Project newProjectResult = openShiftClient.projects().create(newProject);
            LOG.info(String.format("Project %s created", newProjectResult.getMetadata().getName()));
            return newProjectResult;
        } catch (KubernetesClientException e) {
            if (e.getMessage().contains("already exists")) {
                LOG.info(String.format("Project %s already exists", name));
                return openShiftClient.projects().withName(name).get();
            } else {
                LOG.error(e.getMessage());
                return null;
            }

        }
    }

    public boolean deleteProject(String name) {
        boolean result = openShiftClient.projects().withName(name).delete();
        if (result == true) {
            LOG.info(String.format("Project %s deleted", name));
            return true;
        } else {
            LOG.info(String.format("Project %s failed to delete", name));
            return false;
        }
    }

    private List<EnvVar> buildEnvVarList(Map<String,String> envVars){
        List<EnvVar> envVarList = new ArrayList<>();
        if (envVars != null && envVars.size() >0){
            for (Map.Entry<String,String> entry : envVars.entrySet()){
                envVarList.add( new EnvVarBuilder().withName(entry.getKey()).withValue(entry.getValue()).build());
            }
        }
        return  envVarList;
    }

}
