package com.rhc;

import java.io.Serializable;

public class UpdateBuildConfigGitRefCommand implements Serializable {

    public final String namespace;
    public final String buildConfigName;
    public final String gitRef;

    public UpdateBuildConfigGitRefCommand(String namespace, String buildConfigName, String gitRef) {
        this.namespace = namespace;
        this.buildConfigName = buildConfigName;
        this.gitRef = gitRef;
    }

    @Override
    public String toString() {
        return "UpdateBuildConfigGitRefCommand{" +
                "namespace='" + namespace + '\'' +
                ", buildConfigName='" + buildConfigName + '\'' +
                ", gitRef='" + gitRef + '\'' +
                '}';
    }
}
