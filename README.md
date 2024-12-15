# tpcf-workshop
A workshop for Tanzu Platform on Cloud Foundry, using Spring Boot, Spring Cloud, Spring AI and services marketplace

There are 4 demos in this repo:

1. `tpcf-ers-demo` shows the basic `cf push`, `cf create-service` and `cf bind-service` experience.
2. `cloud-native-polls` by Alexandre Roman demos the power of Spring Cloud Discovery and Spring Cloud Config. It also leverages hiding apps in the `apps.internal` domain to prevent exposting them to the outside world. Note that this demo requires access to the config repo at https://github.com/odedia/cloudnativepoll-config
3. `animal-rescue` shows the power of Spring Cloud Gateway in the platform.
4. `pdf-analyzer` demosntrates the use of the Generative AI tile along with pgVector.

There is also a fifth demo under `optional-cloud-workshop` that provides a step-by-step workflow that also explains how to write the code itself.
