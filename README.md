# Profile Hub
[![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

# Importing Data
The Profile Service provides an API for importing profiles into the system.

The main web service is ```[host]/import/profile```, which takes a JSON request (as a POST) with the following structure:

```
{
    "opusId": "",
    "profiles":[{
        "scientificName": "",
        "links":[{
            "creators": [""],
            "edition": "",
            "title": "",
            "publisherName": "",
            "fullTitle":"",
            "description": "",
            "url":"",
            "doi":""
            }],
        "bhl":[{
            "creators": [""],
            "edition": "",
            "title": "",
            "publisherName": "",
            "fullTitle":"",
            "description": "",
            "url":"",
            "doi":""
            }],
        "attributes": [{
            "creators": [""],
            "editors": [""],
            "title": "",
            "text": ""
            }]
    }]
}
```

NOTES:

* The import service will not allow two profiles with the same scientific names.

The response will be a JSON document mapping Scientific Name to a status. For example:
```
{
    Name1: Success,
    Name2: Exists,
    Name3: Failed
    ...
}
```

## Creating a new collection
This section lists the steps required to create a completely new collection and import data. You will need to be an ALA Administrator to create a new collection.

1. Create 2 Data Resources in the ALA Collections admin UI (one for the organisation, one for its images)
1. Create a new collection in Profiles Hub, selecting the data resource of the organisation
1. Edit the opus and add the data resource for the images as an approved image source
1. Create a script that parses your existing data set and produces the JSON document to be sent to the import profile web service
1. Generate a CSV file containing a mapping between Scientific Name and URL for each image in your dataset
1. Execute the script against the profile service's web service
1. Upload the image file to the ALA Collections admin interface, then trigger the injest process.

# Design
This document covers the design considerations of both the Profile Hub and the Profile Service.
## URL Patterns
### Profile Hub

Due to limitations of the ala-cas-client library used to intercept HTTP requests and redirect where necessary to the login page, we cannot use true RESTful URL patterns and rely on the HTTP verb. Therefore, create, update and delete URLs all end with the verb. If in future the ala-cas-client library is updated to handle the HTTP verb, these URLs can simply be modified to remove the verb: no other changes should be necessary. All URLs are mapped with the appropriate HTTP verb as well as having the verb in the URL itself.

* ```/```
  * Public
  * GET - index page, lists all collections
  * PUT, POST, DELETE not allowed
* ```/opus```
  * Public
  * GET - index page, lists all collections
  * PUT, POST, DELETE not allowed
* ```/opus/create```
  * Secured
  * GET - shows the create opus page
  * PUT - creates a new opus
  * POST, DELETE not allowed
* ```/opus/[opusId]```
  * Public
  * GET - view opus page
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/update```
  * Secured
  * GET - shows the update opus page
  * POST - update opus
  * GET, PUT, DELETE not allowed
* ```/opus/[opusId]/delete```
  * Secured
  * DELETE - delete opus
  * PUT, GET, POST not allowed
* ```/opus/[opusId]/profile```
  * Public
  * GET - lists all profiles in the opus
  * PUT, POST, DELETE not allowed
* ```/profile/search```
  * Public
  * GET - search for a profile within an opus or list of opuses
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/create```
  * Secured
  * PUT - create a new profile in the opus
  * POST, GET, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]```
  * Public
  * GET - view profile page
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/update```
  * Secured
  * GET - shows the update profile page
  * POST - update profile
  * DELETE, PUT not allowed
* ```/opus/[opusId]/profile/[profileId]/delete```
  * Secured
  * DELETE - deletes the profile
  * GET, POST, PUT not allowed
* ```/opus/[opusId]/profile/[profileId]/json```
  * Public
  * GET - view the profile in json format
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/attribute```
  * Public
  * GET - lists all attributes for the profile
  * PUT - creates a new attribute
  * POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/attribute/[attributeId]```
  * Public
  * GET - retrieves the attribute
* ```/opus/[opusId]/profile/[profileId]/attribute/[attributeId]/update```
  * Secured
  * POST - updates the attribute
  * GET, DELETE, PUT not allowed
* ```/opus/[opusId]/profile/[profileId]/attribute/[attributeId]/delete```
  * Secured
  * DELETE - deletes the attribute
  * GET, POST, PUT not allowed
* ```/opus/[opusId]/profile/[profileId]/images```
  * Public
  * GET - retrieves images for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/classifications```
  * Public
  * GET - retrieves classifications for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/lists```
  * Public
  * GET - retrieves lists for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/speciesProfile```
  * Public
  * GET - retrieves the species profile for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/links```
  * Public
  * GET - lists all links for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/links/update```
  * Secured
  * POST - updates all links for the profile
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/bhlLinks```
  * Public
  * GET - lists all bhl links for the profile
  * PUT, GET, DELETE not allowed
* ```/opus/[opusId]/profile/[profileId]/bhlLinks/update```
  * Secured
  * POST - updates all bhl links for the profile
  * PUT, GET, DELETE not allowed
* ```/opus/[opusId]/vocab/[vocabId]```
  * Public
  * GET - retrieves vocab for the opus
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/vocab/[vocabId]/update```
  * Secured
  * POST - updates the vocab
  * PUT, GET, DELETE not allowed
* ```/opus/[opusId]/vocab/[vocabId]/findUsages```
  * Public
  * GET - finds usages of the term
  * PUT, POST, DELETE not allowed
* ```/opus/[opusId]/vocab/[vocabId]/replaceUsages```
  * Secured
  * POST - replaces all usages of a term with another term
  * PUT, GET, DELETE not allowed
* ```/opus/[opusId]/users/update```
  * Secured
  * POST - updates admin/editors associated with the opus
  * PUT, GET, DELETE not allowed
* ```/bhl/[pageId]```
  * Public
  * GET - retrieves the page from BHL
  * PUT, POST, DELETE not allowed
* ```/dataResource/```
  * Public
  * GET - lists all data resources
  * PUT, POST, DELETE not allowed
* ```/dataResource/[dataResourceUid]```
  * Public
  * GET - retrieves a single data resource
  * PUT, POST, DELETE not allowed
* ```/user/search```
  * Secured
  * GET - finds users
  * PUT, POST, DELETE not allowed
* ```/audit/object/[objectId]```
  * Secured
  * GET - retrieves audit history for the object
  * PUT, POST, DELETE not allowed

### Profile Service

# Security
### Implementation

#### Authentication
Authentication is controlled via CAS URL pattern matching (see below for the patterns).

#### Authorisation
Authorisation is implemented by way of a custom annotation (```src/java/au.org.ala.profile.security.Secured```) that is
applied to the controller actions where authorisation is required, and a filter (```grails-app/conf/au.org.ala.profile.filter.AccessControlFilters```).
Whenever a request is made to the server, the filter:

* intercepts the request
* finds the controller action method
* checks if it has the Secured annotation
* if the annotation's opusSpecific field is set:
  * looks up the opus (all requests must have an opusId parameter. The parameter name can be overridden with the opusIdParam property of the annotation).
  * checks if the user is an Admin or Editor for that opus
* checks the user's role:
  * If the user is an ALA Admin, they are authorised for all requests
  * If the action requires the ROLE\_PROFILE\_ADMIN role, the user must be in the Admin list for the opus
  * If the action requires the ROLE\_PROFILE\_EDITOR role, the user must be in the Editors list for the ops

## Profile Hub
There are 4 roles: ALA\_ADMIN, ADMIN, EDITOR, USER. The level of access is as follows: ALA\_ADMIN > ADMIN > EDITOR > USER. I.e. an ALA\_ADMIN can do everything an ADMIN can do, and an ADMIN can do everything that and EDITOR can do, etc.

|Action|Required Role|Notes|
|------|-------------|-----|
|Create collection|ALA_ADMIN| |
|View collection|USER| |
|Edit Collection|ADMIN| |
|Delete Collection|ALA_ADMIN| |
|Add new Profile|EDITOR| |
|View Profile|USER| |
|Edit Profile|EDITOR| |
|Delete Profile|EDITOR| |

Admins and Editors are defined per-opus and only have permissions for that particular opus.

### Secured URL Patterns
```
/.*/update.*, /.*/create.*, /.*/delete.*, /user/.*, /audit/.*
```

## Profile Service
Services exposed by the Profile Service are categorised into two buckets: secured and public.

Secured services require an API Key and are intended to only be called by the Profile Hub.

Public services require no form of authentication.

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