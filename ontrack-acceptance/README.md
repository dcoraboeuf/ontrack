How to run acceptance tests?

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
