# Changelog
All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](http://keepachangelog.com/en/1.0.0/)
and this project adheres to [Semantic Versioning](http://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- CSRF protection
### Changed
- New CI database

## [0.9.0]
### Changed
- Updated to use the usgswma/openjdk image and build the project in the Dockerfile
- Updated POM dependencies

## [0.8.0]
### Changed
- Using Spring Boot
- Docker container created
- Load environment variables via entrypoint script
- Setup Jenkinsfiles for build and deploy jobs
- Set "nldi" as the context path for the service
- Retrofit to build via Jenkins shared libraries

[Unreleased]: https://github.com/ACWI-SSWD/nldi-services/compare/nldi-services-0.9...master
[0.9.0]: https://github.com/ACWI-SSWD/nldi-services/compare/nldi-services-0.8...nldi-services-0.9
[0.8.0]: https://github.com/ACWI-SSWD/nldi-services/compare/nldi-services-0.7.0...nldi-services-0.8
