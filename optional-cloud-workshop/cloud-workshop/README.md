# Cloud Lab

Cloud Native Lab - Simple Workshop demonstrating Cloud-Native development with Spring Boot and Tanzu Application Service.

# Items covered:

## Spring Boot

* Initializr
* Web
* Actuator
* Profiles
* Cache
* Data
* Data Migration (Flyway)
* Scheduling
* Messages (AMQB)

## TPCF

* Deployment
* Scaling / Auto-Scaling
* Self-Healing
* Metrics
* Logging
* Market Place
* MySQL
* Redis
* RabbitMQ


## TPCF

## Prerequisites

### Modern Java JDK Installed (at-least Java17)

### Cloud Foundry Command Line Interface (cf CLI) installed

[https://github.com/cloudfoundry/cli/releases](https://github.com/cloudfoundry/cli/releases)

### Java IDE of choice ..

Intellij/Eclipse recommended. Community edition is free.

### Access to a Tanzu Application Service instance

Access will be provided during the workshop.

## 0 - Initialization

Key points:
* Spring Boot Initializr
* Maven vs Gradle
* Maven Wrapper / Gradle Wrapper

### 0.1 - Generate a Spring Boot Template from https://start.spring.io
Or, just the pre-built skeleton at the folder `workdir`.
Stick to the default settings, however update:
- artifact name to cloud-lab
- for dependencies add *Web*
- select Gradle or Maven Project

<img src="img/init-screen.png">

Download it, and unzip it.

Note the mvnw or gradlew files:
These are the Wrapper components for Maven and Gradle: ensuring build script version consistency, removing the dependency of having these build tools installed , and simplifying CI build agent dependencies (only a JDK will be required).

### 0.2 - Import the project into your IDE

Note make sure to do an *import* and not just *open* , to allow for your IDE to correctly pull down dependencies.

## 1 - WebApplication with SpringBoot

Key points:
* Spring Boot Web
* Creating a Simple Helloworld endpoint
* Running the App locally

### 1.1 - Implement a HelloWorld endpoint
This can be done by creating a *HelloWorldController* Java class file with:


```java
@RestController
public class HelloWorldController {

    @RequestMapping("/")
    public String helloWorld(){
        return "Hola Mundo!";
    }
}
```

### 1.2 - Run the application

For maven:

```sh
./mvnw spring-boot:run
```

To build the artifact:

Run:

```sh
./mvnw package
```

```sh
java -jar ./target/cloud-lab-0.0.1-SNAPSHOT.jar
```

Note Tomcat is embedded inside of the build artifact (you don't need an external application Server).

Also note, the different default output directories for Maven and Gradle.

### 1.3 - Test the /hello endpoint
The address will be: localhost:8080/hello

You can test via a browser or commandline:
```sh
curl localhost:8080/hello
```

## 2 - WebApplication on TPCF

Key points:
* Deploying to TPCF
* Scaling in TPCF

### 2.1 - Login into the TPCF instance that you are using (if required)

```sh
cf login -a ENTER_API_URL_HERE
```

Enter your Username and Password.

### 2.2 - Deploy your application to TPCF

Make sure you have the following `manifest.yml` at the root of the project:

```yml
applications:
  - name: cloud-lab
    path: target/cloud-lab-0.0.1-SNAPSHOT.jar
    env:
      JBP_CONFIG_OPEN_JDK_JRE: '{ jre: { version: 17.+ } }'
```

For Maven:
```sh
cf push
```

This will automatically create a new application in your default TPCF development space, with the specific jar artifact deployed.

Note that TPCF will automatically detect that this is a Java application, and use the appropriate *BuildPack*.

### 2.3 - Login into the TPCF portal to view your newly deployed / created application

Open your browser and go to: 

https://apps.sys.(FQDN)

Click through to your app by selecting the default space and org.

Your route to the application (URL) will be presented besides your application.

### 2.4 - Test the /hello endpoint at this route <ROUTE>/hello

### 2.5 - Scale the App

Either provision more instances or more space.

This can be done via the command line or via the GUI.

To scale up to 2 instances:

```sh
cf scale cloud-lab -i 2
```

Via the GUI observe additional instances being spun up.

## 3 - Operations with Spring Boot

Key points:
* Spring Boot Actuator - including walk-through of available endpoints
* Spring Boot Dependency Management (no need for individual artifact versioning)
* Exposing additional Actuator information
* Build / and GIT information

The Spring Actuator Dependency adds out-of-the-box endpoints for monitoring and interacting with your application.

### 3.1 - Add the Spring Boot Actuator dependency to your build script.

The full name of the dependency is : *org.springframework.boot:spring-boot-starter-actuator*

For Maven:

```xml
<dependencies>
    <!-- other dependencies -->

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

     <!-- other dependencies -->
</dependencies>
```
or with Maven:

```sh
./mvnw spring-boot:run
```

### 3.2 - Check the localhost:8080/actuator/health endpoint

Verify that it works on your local running instance of the app:

```sh
curl localhost:8080/actuator/health
```

You can also use a browser

### 3.3 - Expose additional information on the Health Endpoint

To the application.properties file in resources add:

```properties
management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

Later on in the Workshop this endpoint will also show external dependency status and information (including Data, Cache, and Messaging).

### 3.4 - Check for new information on the health endpoint

This will require running rebuilding the application:

```sh
curl localhost:8080/actuator/health
```

### 3.5 - Enable ALL Actuator endpoints

Currently exposed actuator endpoints can be viewed at: http://localhost:8080/actuator

For security reasons, many of the these endpoints are turned off by default.

They can be ALL enabled by adding the following to your application.properties file:

```properties
management.endpoints.web.exposure.include=*
```

Rebuild, and check the http://localhost:8080/actuator endpoint for available ones.

### 3.6 - BONUS - Add build information the /info endpoint

We want to be able to easily view build information from running instances of our app.

You will need to generate a META-INF/build-info.properties in your class path .. this can be automated :

Add the following to your build.gradle:

Or for Maven in the pom.xml .. update the build block to the following .. note the addition of the executions block:


```xml
<build>
	<plugins>
		<plugin>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-maven-plugin</artifactId>
			<executions>
				<execution>
					<goals>
						<goal>build-info</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>
```

Rebuild, and check the http://localhost:8080/actuator/info endpoint.

### 3.7 - BONUS - Add GIT Information to the /info endpoint

Hint - you will need to init a GIT repo locally, and add the com.gorylenko.gradle-git-properties dependency in Gradle.

For Maven users, the spring-boot-starter-parent POM includes a pre-configured plugin to generate a git.properties file. To use it, add the following declaration to your POM:

```xml
<build>
	<plugins>
		<plugin>
			<groupId>pl.project13.maven</groupId>
			<artifactId>git-commit-id-plugin</artifactId>
		</plugin>
	</plugins>
</build>
```

## 4 - Operations on TPCF

Key points:
* Dealing with Crashes
* Metric and Logging
* Auto-Scaling
* Zero down-time deployments

### 4.1 - Add an endpoint to your App to simulate JVM crashes

Add a new KillController with a Kill endpoint/

```java
@RestController
public class KillController {

  @RequestMapping("/kill")
  public void kill(){
      System.exit(1);
  }
}
```

Rebuild your app, and redeploy to TPCF.

```sh
 ./mvnw clean package && cf push
```

### 4.2 - In separate terminal window, TAIL the TPCF app logs

```sh
cf logs cloud-lab --recent
cf logs cloud-lab
```

### 4.3 - Call the /kill endpoint

Note that TPCF will automatically bring up a new instance.

This can be monitored from the TPCF Dev Portal.

You can also view what happened in the logging window from the previous step.

For additional information, you can also drill down into the *TPCF Metrics" option in the Application page in TPCF. This includes more in-depth logging, and crash analysis.

### 4.5 - BONUS - Add Auto-Scaling to Your Application

https://docs.run.pivotal.io/appsman-services/autoscaler/using-autoscaler.html

### 4.6 - BONUS - Perform a Blue-Green Deployment to Reduce Downtime and Risk

Blue-green deployment is a technique that reduces downtime and risk by running two identical production environments called Blue and Green.

At any time, only one of the environments is live, with the live environment serving all production traffic. For this example, Blue is currently live and Green is idle.

#### 4.6.1 - BONUS - Deploy a new instance of our cloud-lab

```sh
cf push cloud-lab-2
```

Right now, we have 2 deploys apps running (they can be different version of the application).

#### 4.6.2 - Route all cloud-lab subdomain traffic to cloud-lab2 (in addition to our original cloud-lab instance).

For reference, current Routes can be viewed with:

```sh
cf routes
```

Note the domain used for your cloud-lab apps.

Route cloud-lab subdomain traffic to cloud-lab:

```sh
 cf map-route cloud-lab-2 ENTER_TPCF_DOMAIN --hostname cloud-lab
```

**Replace ENTER_TPCF_DOMAIN with domain from *cf routes* step.**

##### 4.6.3 - Unmap cloud-lab traffic to the cloud-lab subdomain

```
cf unmap-route cloud-lab ENTER_TPCF_DOMAIN --hostname cloud-lab
```

Note, all cloud-lab subdomain traffic will now be mapped to our recent deploy.

## 5 - Configuration with Spring Boot

Key points:
* Spring Boot Profiles
* Configuration precedence

Spring Boot lets you externalize your configuration so that you can work with the same application code in different environments.

https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html

### 5.1 - Allow injection of the helloWorld message via external sources
```java
@RestController
public class HelloWorldController {

    @Value("${helloworld.message:'Shalom Olam - default!'}")
    private String helloMessage;

    @RequestMapping("/")
    public String helloWorld(){
        return helloMessage;
    }
}
```

Note #1 : the usage of the default value in case nothing is found (optional).

### 5.2 - Add a configurable value for helloworld.message in the application.properties file.

```properties
helloworld.message="Hello World - default config file"
```

### 5.3 - Rebuild / Restart your app, and verify the /hello endpoint.

Default should be "Hello World - default config file"

## 6 - Configuration on TPCF

Key points:
* The cloud profile
* Spring Cloud Config Server overview

### 6.1 - Add a custom HelloWorld message for cloud deployments

TPCF deploys will automatically load *cloud* profile settings.

In the resources folder, add a *application-cloud.properties* file.

Add:

```properties
helloworld.message="Hello World - cloud only"
```

Build and Redeploy to TPCF.

Verify the updated message at the /hello endpoint.

Note: You can also use the Spring Config Server (available as a Service in the MarketPlace) to inject properties from an external source such as a GitHub repo - more on that later!

## 7 - Caching with Spring Boot

Key points:
* Spring Boot Cache
* The Cacheable annotation

### 7.1 - Add a slow , costly endpoint to the Application.

One example would be performing an uppercase operation on a String with a time delay.

```java
@RestController
public class CacheExampleController {

  @RequestMapping("/uppercase")
  public String uppercase(String input ){
      try {Thread.sleep(5000); } catch (InterruptedException e) {}

      return input.toUpperCase();
  }
}
```

Rebuild, rerun the app.

From the browser you can call : *http://localhost:8080/uppercase?input=test*

Or using curl:

```sh
curl http://localhost:8080/uppercase?input=test
```

Note how the /uppercase endpoint is always slow.

### 7.2 - Add the Spring Boot Cache dependency to your build script.

The full name of the dependency is : *org.springframework.boot:spring-boot-starter-cache*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
    //...
    compile('org.springframework.boot:spring-boot-starter-cache')
    //...
}
```

### 7.3 - Enable Caching on the endpoint by using the *Cacheable* Annotation.

Updated CacheExampleController should look like:

```java
@Cacheable("uppercase")
@RequestMapping("/uppercase")
public String uppercase(String input ){
    try {Thread.sleep(5000); } catch (InterruptedException e) {}

    return input.toUpperCase();
}
```

You will also need to turn on Caching (app wide) by adding the EnableCaching annotation to your app.

This can be done in the CloudLabApplication class:

```java
@SpringBootApplication
@EnableCaching
public class CloudLabApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudLabApplication.class, args);
	}
}
```

Restart your app, and verify that subsequent calls to the endpoint return much quicker.

Note, the default cache implementation uses local in-memory caching, but this can be easily change to use 3rd party caching solutions such as Redis.

## 8 - Caching on TPCF

Key points:
* Spring Boot Redis
* CF MarketPlace / Service Broker
* Binding Services to App Instances
* Creating a Redis Instance in TPCF


### 8.1 - Add the Spring Boot Redis and commons-pool dependencies to your build script.

If using Gradle, your new dependency block should look like:

```maven
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-redis</artifactId>
    </dependency>
```

Rebuild, and redeploy to TPCF.

### 8.2 - Create a Redis Service Instance in TPCF

You can view available self-self / on-demand provisioning services via the marketplace.

```sh
cf marketplace
```

To create a Redis Service run:

```sh
cf create-service p-redis shared-vm custom-redis
```

### 8.3 - Bind the Service to our application

```sh
cf bind-service cloud-lab custom-redis
```

Restage your app:
```sh
cf restage cloud-lab
```

Confirm connection to your Redis Server using the health endpoint: /actuator/health

Also confirm that cache is still working.

### 8.4 - BONUS - Default to Simple (non-Redis caching locally) and Redis Caching in the cloud

In application.properties add:

```properties
spring.cache.type=SIMPLE
```

In application-cloud.properties add:

```properties
spring.cache.type=REDIS
```

Verify that Caching works locally and in TPCF.

## 12 BONUS - Scheduling with Spring Boot

https://github.com/joshlong-attic/cf-task-demo

## 13 - Messaging with Spring Boot

Key points:
* Spring Boot AMQP
* Sending and Receiving Messages

### 13.1 - Add the Spring Boot AMQP dependency to your build script.

The full name of the dependency is : *org.springframework.boot:spring-boot-starter-amqp*

If using Gradle, your new dependency block should look like:

```groovy
dependencies {
    //...
    compile('org.springframework.boot:spring-boot-starter-amqp')
    //...
}
```

### 13.2 - Create a Controller for Sending and Receiving Queue Messages

In a new QueueController java file , add:

```java
@RestController
public class QueueController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @RequestMapping("/sendmessage")
    public void sendMessage(String input){
        amqpTemplate.convertAndSend("myQueue", input);
    }

    @RequestMapping("/getmessage")
    public String getMessage(){
        Message message = amqpTemplate.receive("myQueue");
        if(message == null)
            return "Queue empty";

        return message.toString();
    }
}

```

This will send messages to the default the "Default" exchange, with the "myQueue" routing key.

*For purpose of workship, unlike Caching / Data , we will go directly to testing in TPCF*

Your RabbitMQ instance will need to have a queue created called **myQueue**. Messages sent to the Default exchange with the **myQueue** routingkey will be automatically routed to the **myQueue** queue.

## 14 - Messaging on TPCF

Key points:
* Creating a RabbitMQ Instance in TPCF

### 14.1 - Create a RabbitMQ Service Instance in TPCF

You can view available self-self / on-demand provisioning services via the marketplace.

```sh
cf marketplace
```

To create a RabbitMQ Service run:

```sh
cf create-service p-rabbitmq standard custom-rabbitmq
```

### 14.2 - Bind the Service to your application

```sh
cf bind-service cloud-lab custom-rabbitmq
```

### 14.3 - Add the *myQueue* queue to your RabbitMQ instance

In the TPCF Dev Portal:

In your DevSpace, select your RabbitMQ instance, and click Manage.

This will pull up the RabbitMQ control GUI.

<img src="img/queue-screen.png">

Under the Queues tab, select add a new queue.

Call it "myQueue".

The Default Exchange will route to specific queues based on specified routing key.

Confirm connection to your RabbitMQ Server using the health endpoint: /actuator/health

Rebuild and deploy your app to TPCF , with the changes from the previous step.


### 14.4 - Test messaging using the /getmessage and /sendmessage endpoints.

To send message, curl or point your browser to /sendmessage?input=YOUR_MESSAGE_STRING

To receive messages, curl or point your browser to /getmessage

## 15 - Spring Cloud

**Discussion Item**

Spring Cloud provides tools for developers to quickly build some of the common patterns in distributed systems (e.g. configuration management, service discovery, circuit breakers, intelligent routing, micro-proxy, control bus, one-time tokens, global locks, leadership election, distributed sessions, cluster state). Coordination of distributed systems leads to boiler plate patterns, and using Spring Cloud developers can quickly stand up services and applications that implement those patterns. They will work well in any distributed environment, including the developer's own laptop, bare metal data centres, and managed platforms such as Cloud Foundry.

