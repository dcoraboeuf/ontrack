How to run acceptance tests?

## Automated tests in the IDE

Make sure the `ontrack-web` module runs with the `it` Maven profile:

```
mvn jetty:run -P it
```

This will run the application at http://localhost:9999/ontrack. The port can be overridden using the `jetty.port` system
property:

```
mvn jetty:run -P it -Djetty.port=9080
```

You can also bypass this and run the tests against an instance running somewhere else.

Then you can run the `net.ontrack.acceptance.JBehaveTestCase` JUnit test individually or all the unit tests in
the `ontrack-acceptance` module. The following system properties must be associated with the run:

* `webdriver.base.url` - the URL the application is running at, typically http://localhost:9999/ontrack

## Automated tests only

In the `ontrack-acceptance` project:

```
mvn clean install -P it -Dwebdriver.base.url=...
```

The `webdriver.base.url` must contain the URL the application is deployed on (for example: http://localhost:8080/ontrack)

## Automated tests with automatic deployment on a local Jetty

In the `ontrack-acceptance` project:

```
mvn clean install -P it P it-jetty
```

The application will automatically be deployed on http://localhost:9999/ontrack. One can optionally define another port
by defining the `itPort` system property:

```
mvn clean install -P it P it-jetty -DitPort=9080
```

Note that the `ontrack-web` module must be installed with the same version than the `ontrack-acceptance` one.

One can change the version to be deployed by specifying the `ontrackVersion` system property:

```
mvn clean install -P it P it-jetty -DontrackVersion=1.8
```

## Headless considerations

When running in a headless environment, one display must be made available for the run of the integration tests.
The `ontrack-acceptance` module does not take charge of setting up such an environment and the infrastructure
must do it.

A typical solution is to use the [Jenkins Xvnc plug-in](https://wiki.jenkins-ci.org/display/JENKINS/Xvnc+Plugin) when
running with Jenkins.