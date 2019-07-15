[![Build status](https://travis-ci.org/servicecatalog/oscm-identity.svg?branch=master)](https://travis-ci.org/servicecatalog/oscm-identity)
[![codecov](https://codecov.io/gh/servicecatalog/oscm-identity/branch/master/graph/badge.svg)](https://codecov.io/gh/servicecatalog/oscm-identity)

# oscm-identity
Identity service for managing authentication with external IdPs using OpenId Connect

## Building from source
To build the application
1. Download and install lombok for you IDE. Instructions for Eclipse and IntelliJ can be found [here](https://www.baeldung.com/lombok-ide).     
2. Run `mvn clean package`

## Starting the application
1. Copy *config* directory to the same path, where You've put Your JAR file
2. Provide default tenant configuration in *config/tenants/tenant-default.properties*
3. Start the application using *java -jar* command