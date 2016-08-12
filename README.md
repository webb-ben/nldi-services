# NLDI REST (like) Services

[![Build Status](https://travis-ci.org/ACWI-SSWD/nldi-services.svg?branch=master)](https://travis-ci.org/ACWI-SSWD/nldi-services)

##Public API
The services are accessed via an http GET request. All navigation output is generated as GeoJSON ("application/vnd.geo+json")

#### The root is {host}/nldi.
Both a test and a production are exposed to the public:
__https://cida-test.er.usgs.gov/nldi__ is the test root.
__https://cida.usgs.gov/nldi__ is the production root.
This endpoint will give you the valid dataSource names for the other endpoints. There is documentation at /about and a demo UI at /about/demo.

#### Display Up/Down Stream Flow Lines
/{featureSource}/{featureSourceId}/navigate/{navigationMode} where:
* __{featureSource}__ identifies the source used to start navigation:
  * __comid__ start the navigation from an NHDPlus comid
  * any of the network linked feature sources (listed at /)
* __{featureSourceId}__ the NHDPlus comid or feature from which to start the navigation
* __{navigationMode}__ is the direction and type of navigation:
  * __DD__ is __D__ownstream navigation with __D__iversions
  * __DM__ is __D__ownstream navigation on the __M__ain channel
  * __PP__ is __P__oint to __P__oint navigation (the __stopComid__ query parameter is required and must be downstream of the __{comid}__)
  * __UM__ is __U__pstream navigation on the __M__ain channel
  * __UT__ is __U__pstream navigation including all __T__ributaries

#### Display Up/Down Stream Events
/{featureSource}/{featureSourceId}/navigate/{navigationMode}/{dataSource} where:
* __{featureSource}__ identifies the source used to start navigation  (same values as for Flow Lines)
* __{featureSourceId}__ the NHDPlus comid or other feature from which to start the navigation
* __{navigationMode}__ is the direction and type of navigation (same values as for Flow Lines)
* __{dataSource}__ is the abbreviation of the data source from which events should be shown

#### Query Parameters
Both endpoints accept the same query parameters to further refine/restrict the navigation being requested
* __distance={dist}__ limit the navigation to __{dist}__ kilometers from the starting point
* __stopComid={stopid}__ for use with __PP__ navigation between the __{featureSourceId}__ and __{stopid}__
  * (only applicable to NHDPlus comid navigation and the __{stopid}__ must be downstream of the __{featureSourceId}__)

##Developer Environment

[nldi-db](https://travis-ci.org/ACWI-SSWD/nldi-db) contains everything you need to set up a development database environment. It includes data for the Yahara River in Wisconsin.

Note that this project has some integration testing against the database. The "package" goal of the maven command will stop the build before running them.

