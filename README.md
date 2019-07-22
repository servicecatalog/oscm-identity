[![Build status](https://travis-ci.org/servicecatalog/oscm-identity.svg?branch=master)](https://travis-ci.org/servicecatalog/oscm-identity)
[![codecov](https://codecov.io/gh/servicecatalog/oscm-identity/branch/master/graph/badge.svg)](https://codecov.io/gh/servicecatalog/oscm-identity)

# oscm-identity
Identity Service for managing authentication with external IdPs using [OpenID Connect](https://openid.net/connect/)

## Building from source
To build the application
1. Download and install [lombok](https://projectlombok.org/) for you IDE. Instructions for Eclipse and IntelliJ can be found [here](https://www.baeldung.com/lombok-ide).     
2. Run `mvn clean package`

## Starting the application
1. Copy *config* directory to the same path, where You've put Your JAR file

2. Provide default tenant configuration in *config/tenants/tenant-default.properties* where:
```properties
oidc.provider=external identity provider's name
oidc.clientId=oscm-identity must be register in external identity provider, this is the id of such registered application
oidc.authUrl=url which your external identity provider uses for authenticating users
oidc.logoutUrl=url which your external identity provider uses for logging-out users
oidc.idTokenRedirectUrl=url which is used for id_token callback, must be register in your external identity provider list of URL responses
```
3. Start the application using `java -jar` command

## Setup with Docker
The Identity Service is included with the OSCM Docker installation. Find the [description in the oscm-dockerbuild repository](https://github.com/servicecatalog/oscm-dockerbuild#quick-start-oscm-with-docker) on how to install OSCM with docker-compose. 

