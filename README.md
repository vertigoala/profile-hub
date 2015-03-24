# Profile Hub
[![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

# Design

# Security
## Profile Hub
There are 4 roles: ALA\_ADMIN, ADMIN, EDITOR, USER. The level of access is as follows: ALA\_ADMIN > ADMIN > EDITOR > USER. I.e. an ALA\_ADMIN can do everything an ADMIN can do, and an ADMIN can do everything that and EDITOR can do, etc.

|Action|Required Role|Notes|
|------|-------------|-----|
|Create collection|ALA_ADMIN| |
|View collection|All| |
|Edit Collection|ADMIN| |
|Delete Collection|ALA_ADMIN| |
|Add new Profile|EDITOR| |
|View Profile|USER| |
|Edit Profile|EDITOR| |
|Delete Profile|EDITOR| |

Admins and Editors are defined per-opus and only have permissions for that particular opus.

## Profile Service
Services exposed by the Profile Service are categorised into two buckets: secured and public.

Secured services require an API Key and are intended to only be called by the Profile Hub.

Public services require not form of authentication.

# Testing

## Grails

The grails/groovy code is tested using the Spock framework. Tests live in the ```tests/unit/``` and ```tests/integration``` directories.


## Angular JS

The UI layer of the profile hub is written in AngularJS. We use the Jasmine test framework and the Karma test runner to test the Angular code.
This guide tells how to install and run the tests on Mac OS (tested with Yosemite).

### Install Karma
1. Navigate to the root directory of the profile-hub project
1. Execute the following:
   1. ```brew install node.js```
   1. ```npm install karma```
   1. ```npm install karma-jasmine karma-chrome-launcher --save-dev```
   1. ```npm install -g karma-cli```
   1. ```npm install karma-coverage```
1. Install other optional browser launchers:
   1. ```npm install karma-firefox-launcher```
   1. ```npm install karma-safari-launcher```
   1. ```npm install karma-ie-launcher```
   1. Update the ```browsers: ['Chrome']``` property in karma.conf.js to add additional browsers to test against.
1. The source repository contains a karma.conf.js file: this is configures the Karma test runner
1. (Optional) Install the IntelliJ Karma test runner:
  1. Preferences -> Plugins -> Search for Karma -> Install (at the time of writing there was only 1 matching plugin)
  1. Create a run configuration:
     1. Run -> Edit Configurations -> New -> Karma
     1. Give it a name
     1. Select the karma.conf.js file from the root of the project directory as the Configuration File
     1. The other default values are fine.

### Writing Tests
The karma.conf.js file contains a list of all Javascript resources required for testing in the ```files``` list. If you create a new file, *you must update the karma.conf.js file* or your tests won't work.
```
// list of files / patterns to load in the browser
files: [
    'web-app/js/thirdparty/angular-1.3.13.min.js',
    'web-app/js/thirdparty/angular-mocks-1.3.13.js',
    'web-app/js/thirdparty/ui-bootstrap-tpls-0.12.0.js',
    'web-app/js/angular/profiles.js',
    'web-app/js/angular/utils/*.js',
    'web-app/js/angular/services/*.js',
    'web-app/js/angular/controllers/*.js',
    'test/js/specs/**/*.js'
],
```
*The order is important!*

Jasmine tests live in the ```tests/js/specs/``` directory. Update existing files or add new ones as necessary.

### Code Coverage

You can see a code coverage report for the javascript files by following these steps:

1. Command Line
   1. Run ```karma start```
   1. Open ```coverage/<browser>/index.html```
1. IntelliJ
   1. Run the karma configuration using the ```Run 'karma' with Coverage``` option (the 3rd toolbar button after the run dropdown).
   1. Open ```coverage/<browser>/index.html```
   1. IntelliJ will also annotate the filenames in the project explorer with a percentage of lines covered.