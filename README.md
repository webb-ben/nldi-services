# Network Linked Data Index Services

[![Spotless Check](https://github.com/internetofwater/nldi-services/actions/workflows/spotless.yml/badge.svg)](https://github.com/internetofwater/nldi-services/actions/workflows/spotless.yml)
[![codecov](https://codecov.io/gh/internetofwater/nldi-services/branch/master/graph/badge.svg)](https://codecov.io/gh/internetofwater/nldi-services)

This repository houses code behind the Network Linked Data Index (NLDI) API [(Swagger Docs)](https://labs.waterdata.usgs.gov/api/nldi/swagger-ui/index.html). The NLDI is hosted as part of the [USGS Waterdata Labs](https://labs.waterdata.usgs.gov/index.html), a set of new capabilities being developed by the USGS Water Mission Area.

## Public API
The services are accessed via an http GET request. All output is generated as JSON and GeoJSON.

### The root is {host}/api/nldi
Both a test and a production are exposed to the public:
  <https://labs-beta.waterdata.usgs.gov/api/nldi/linked_data> is the test root.
  <https://labs.waterdata.usgs.gov/api/nldi/linked_data> is the production root.

This endpoint will give you the valid dataSource names for the other endpoints. There is also a health check at /about/health and version information at /about/info.

In general, the API uses hypermedia to help discover options from a given endpoint. A summary of these options follows.

### Up/Down Stream navigation
/{featureSource}/{featureSourceId}/navigation/{navigationMode} where:
*   `{featureSource}` identifies the source used to start navigation:
    *   The `comid` `{featureSource}` starts the navigation from an NHDPlus comid
    *   any of the network linked feature sources (listed at /)
*   `{featureSourceId}` the NHDPlus comid or feature from which to start the navigation
*   `{navigationMode}` is the direction and type of navigation:
    *   `DD` is `D`ownstream navigation with `D`iversions
    *   `DM` is `D`ownstream navigation on the `M`ain channel
    *   `PP` is `P`oint to `P`oint navigation (the `stopComid` query parameter is required and must be downstream of the `{comid}`)
    *   `UM` is `U`pstream navigation on the `M`ain channel
    *   `UT` is `U`pstream navigation including all `T`ributaries

### Up/Down Stream data
/{featureSource}/{featureSourceId}/navigation/{navigationMode}/{dataSource} where:
*   `{featureSource}` identifies the source used to start navigation
*   `{featureSourceId}` the NHDPlus comid or other feature from which to start the navigation
*   `{navigationMode}` is the direction and type of navigation
*   `{dataSource}` is the abbreviation of the data source to return

### Query Parameters
Navigations accept query parameters to further refine/restrict the navigation being requested
*   `f=json` if an html media type is requested explicitely, a blank html page is returned. `f=json` will override this html accept header.
*   `distance={dist}` **REQUIRED** limits the navigation to `{dist}` kilometers from the starting point
*   `stopComid={stopid}` for use with `PP` navigation between the `{featureSourceId}` and `{stopid}`
    *   (only applicable to NHDPlus comid navigation and the `{stopid}` must be downstream of the `{featureSourceId}`)

### Other Endpoints
The NLDI includes additional helper endpoints that will be documented here at a later date. See the [swagger documentation](https://labs.waterdata.usgs.gov/api/nldi/swagger-ui/index.html) and [NLDI landing page](https://labs.waterdata.usgs.gov/about-nldi/index.html) for more.

## Development
This is a Spring Batch/Boot project.  All of the normal caveats relating to a Spring Batch/Boot application apply.
In general, do not run this project via Docker locally, since it places everything under root ownership.
Rather, start up the demo db and create an application.yml file as described below, then run the project from your IDE.

### Dependencies
This application utilizes a PostgreSQL database.
[nldi-db](https://github.com/internetofwater/nldi-db) contains everything you need to set up a development database environment. It includes data for the Yahara River in Wisconsin.

### Running the Demo DB for local development
See the nldi-db project for more details, but in short:
```shell
docker network create --subnet=172.26.0.0/16 nldi
docker run -it --env-file ./.env -p 127.0.0.1:5437:5432/tcp usgswma/nldi-db:demo
```
Note the _5437_ port mapping, which is used in the environmental variables below.

### Environment variables
To run the project (connecting to a separately running db instance) you will need to create the file application.yml in the project's root directory and add the following (normal defaults are filled in):
```yaml
nldiDbHost: localhost
nldiDbPort: 5437 #Or whatever port you map it to
nldiDbUsername: [dbUserName] #See nldi-db project .env file 'NLDI_READ_ONLY_USERNAME'
nldiDbPassword: [dbPassword] #See nldi-db project .env file 'NLDI_READ_ONLY_PASSWORD'
nldiDbName: [dbName] #See nldi-db project .env file 'NLDI_DATABASE_NAME'
nldiProtocol: http
nldiHost: owi-test.usgs.gov:8080
nldiPath: /test-url

serverContextPath: /nldi
springFrameworkLogLevel: INFO
serverPort: 8080

spring.security.user.password: changeMe
```

### Testing
This project contains JUnit tests. Maven can be used to run them (in addition to the capabilities of your IDE).

To run the unit tests of the application use:

```shell
mvn package
```

To additionally start up a Docker database and run the integration tests of the application use:

```shell
mvn verify -DTESTING_DATABASE_PORT=5445 -DTESTING_DATABASE_ADDRESS=localhost -DTESTING_DATABASE_NETWORK=nldi
```
