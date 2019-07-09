[![Build status](https://travis-ci.org/servicecatalog/oscm-identity.svg?branch=master)](https://travis-ci.org/servicecatalog/oscm-identity)
[![codecov](https://codecov.io/gh/servicecatalog/oscm-identity/branch/master/graph/badge.svg)](https://codecov.io/gh/servicecatalog/oscm-identity)

# oscm-identity
Identity service for managing authentication with external IdPs using OpenId Connect

## Building the application
To build the application simply run `mvn clean package`

## Starting the application
1. Copy *config* directory to the same path, where You've put Your JAR file
2. Provide default tenant configuration in *config/tenants/tenant-default.properties* where:
```properties
oidc.provider=external identity provider's name
oidc.clientId=oscm-identity must be register in external identity provider, this is the id of such registered application
oidc.authUrl=url which your external identity provider uses for authenticating users
oidc.idTokenRedirectUrl=url which is used for id_token callbcak, must be register in your external identity provider list of URL responses
```
3. Start the application using `java -jar` command
