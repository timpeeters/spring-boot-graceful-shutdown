Spring Boot Graceful Shutdown
=============================

[![Build Status](https://api.travis-ci.org/timpeeters/spring-boot-graceful-shutdown.svg?branch=master)](https://www.travis-ci.org/timpeeters/spring-boot-graceful-shutdown)
[![Known Vulnerabilities](https://snyk.io/test/github/timpeeters/spring-boot-graceful-shutdown/badge.svg?targetFile=pom.xml)](https://snyk.io/test/github/timpeeters/spring-boot-graceful-shutdown?targetFile=pom.xml)
[![Dependabot Status](https://api.dependabot.com/badges/status?host=github&repo=timpeeters/spring-boot-graceful-shutdown)](https://dependabot.com)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.timpeeters/spring-boot-graceful-shutdown/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.timpeeters/spring-boot-graceful-shutdown)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c7e77bfc98a64687aa6a87b1619fb06a)](https://www.codacy.com/app/timpeeters/spring-boot-graceful-shutdown?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=timpeeters/spring-boot-graceful-shutdown&amp;utm_campaign=Badge_Grade)
[![Codacy Badge](https://api.codacy.com/project/badge/Coverage/c7e77bfc98a64687aa6a87b1619fb06a)](https://www.codacy.com/app/timpeeters/spring-boot-graceful-shutdown?utm_source=github.com&utm_medium=referral&utm_content=timpeeters/spring-boot-graceful-shutdown&utm_campaign=Badge_Coverage)

This project adds graceful shutdown behavior to Spring Boot.


Versions
--------

Multiple branches are maintained to support multiple Spring Boot versions.
The following tables show the relation between the Spring Boot version and the Spring Boot Actuator Server Config version.

| Spring Boot | Spring Boot Actuator Server Config | Branch |
| :---        | :---                               | :---   |
| 1.5.x       | 1.0.x                              | 1.0.x  |
| 2.0.x       | 2.0.x                              | 2.0.x  |
| 2.1.x       | 2.1.x                              | master |


Flow
----

1. The JVM receives the SIGTERM signal and starts shutting down the Spring container.
2. A Spring EventListener listens for a ContextClosedEvent and is invoked once the shutdown is started.
3. The EventListener updates a Spring Boot HealthIndicator and puts it "out of service".
5. The context shutdown is delayed using a Thread.sleep to allow the load balancer to see the updated HealthIndicator status and stop forwarding requests to this instance.
7. When the Thread.sleep is finished, the Tomcat container is gracefully shutdown. 
First by pausing the connector, no longer accepting new request.
Next, by allowing the Tomcat thread pool a configurable amount of time to finish the active threads.
8. Finally, the Spring context is closed.


Limitations
-----------

Currently this project only supports Tomcat as embedded web container for Spring Boot. 
Undertow and/or Jetty are not yet supported.


Installation
------------

1. Add the following Maven dependency:

```xml
<dependency>
    <groupId>com.github.timpeeters</groupId>
    <artifactId>spring-boot-graceful-shutdown</artifactId>
    <version>X.X.X</version>
</dependency>
```


Configuration
-------------

| Key                       | Default value  | Description |
| ------------------------- | -------------- | ----------- |
| graceful.shutdown.enabled | false          | Indicates whether graceful shutdown is enabled or not. | 
| graceful.shutdown.timeout | 60s            | The time to wait for active threads to finish before shutting down the Tomcat connector. |
| graceful.shutdown.wait    | 30s            | The time to return "out of service" on the health page before starting the graceful shutdown. |

It is important to specify the time unit, otherwise you end up with milliseconds.

Alternative implementations
---------------------------

We found several alternatives for graceful shutdown behavior in Spring Boot. 

- https://github.com/SchweizerischeBundesbahnen/springboot-graceful-shutdown
- https://github.com/corentin59/spring-boot-graceful-shutdown
- https://github.com/gesellix/graceful-shutdown-spring-boot


References
----------

- https://github.com/spring-projects/spring-boot/issues/4657
