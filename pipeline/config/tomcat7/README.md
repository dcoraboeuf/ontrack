## Installation in stand-alone Tomcat

We assume Tomcat 7 in unzipped in `$TOMCAT_HOME` and that the `tomcat` user (group `tomcat`) is used
to run Tomcat. Change accordingly to your own settings.

### Tomcat configuration

In `$TOMCAT_HOME/lib`, put the following libraries:

* H2 database driver - downloaded from http://search.maven.org/#artifactdetails%7Ccom.h2database%7Ch2%7C1.3.174%7Cjar
* Java mail libraries - downloaded from http://www.oracle.com/technetwork/java/index-138643.html

### Configuration for ontrack

Create the home directory:

```
sudo mkdir /opt/ontrack
sudo chown tomcat:tomcat /opt/ontrack
```

Create the `$TOMCAT_HOME/conf/Catalina/localhost/ontrack.xml` file by copying the file from GitHub at `pipeline/config/tomcat7/context.xml`.

In the `$TOMCAT_HOME/conf/catalina.properties` configuration file, add the following section at the end:

```
# Global custom system properties
spring.profiles.active=prod

# ontrack system properties
ontrack.home=/opt/ontrack/home
```

### Deployment

Upload `ontrack.war` into `$TOMCAT_HOME/webapps`.

### Stopping & starting

Stop the server by using:

```
sudo /etc/init.d/tomcat stop
```

Start the server by using:

```
sudo /etc/init.d/tomcat start
```

Tomcat logs are available at `$TOMCAT_HOME/logs`.

## Installation on Ubuntu

### Tomcat installation

Install tomcat 7 by running:

```
sudo apt-get install tomcat7
```

This should make sure to install the JDK 7 as well.

### Tomcat configuration

In `/usr/share/tomcat7/lib`, put the following libraries:

* H2 database driver - downloaded from http://search.maven.org/#artifactdetails%7Ccom.h2database%7Ch2%7C1.3.174%7Cjar
* Java mail libraries - downloaded from http://www.oracle.com/technetwork/java/index-138643.html

### Configuration for ontrack

Create the home directory:

```
sudo mkdir /opt/ontrack
sudo chown tomcat7:tomcat7 /opt/ontrack
```

In the `/etc/default/tomcat7` configuration file, change the first `JAVA_OPTS` line into:

```
JAVA_OPTS="-Djava.awt.headless=true -Dspring.profiles.active=prod -Dontrack.home=/opt/ontrack/home -Xmx256m -XX:+UseConcMarkSweepGC"
```

Create the `/etc/tomcat7/Catalina/localhost/ontrack.xml` file by copying the file from GitHub at `pipeline/config/tomcat7/context.xml`.

### Deployment

Upload `ontrack.war` into `/var/lib/tomcat7/webapps`.

### Stopping & starting

Stop the server by using:

```
sudo /etc/init.d/tomcat7 stop
```

Start the server by using:

```
sudo /etc/init.d/tomcat7 start
```
