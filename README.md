# Introduction


## Requirements

* Java 8 or greater 
* Maven 3.3.x or greater.
* an OpenShift connection. Locally, just make sure you user is logged in via `oc`. In OpenShift, the app will use the 
service account that is running the app. Make sure this service account has `edit` for the namespace where the
the build config (pipeline) that you will be triggering is
 

## Running Locally

This will build the app, stand it up locally on port 8080, and then watch changes in source code, redeploying as needed.
Make sure you are logged in via `oc`

```
$ mvn compile vertx:run
```

## Deploy to OpenShift

This will build and deploy the app in the namespace your `oc` is logged into. Make sure the `default` service account has
`edit` for the namespace you will be interacting with (e.g where the pipeline is running).

```
$ mvn clean fabric8:deploy -Popenshift
```