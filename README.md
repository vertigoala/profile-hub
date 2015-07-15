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
        "nameAuthor": "",
        "fullName": "",
        "enableNSLMatching": "",
        "nslNameIdentifier": "",
        "nslNomenclatureIdentifier": "",
        "nslNomenclatureMatchStrategy": "",
        "nslNomenclatureMatchData": [""],
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
            }],
        "images": [{
            "title": "(mandatory)",
            "identifier": "(url to the image - mandatory)",
            "creator": "",
            "dateCreated": ""
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

1. Create a Data Resource in the ALA Collections admin UI for the organisation
1. Create a new collection in Profiles Hub, selecting the data resource you just created
1. Create a script that parses your existing data set and produces the JSON document to be sent to the import profile web service
1. Generate a CSV file containing a mapping between Scientific Name and URL for each image in your dataset
1. Execute the script against the profile service's web service
1. Upload the image file to the ALA Collections admin interface, then trigger the ingest process.

# Design
This document covers the design considerations of both the Profile Hub and the Profile Service.


# Security
## Profile Hub

### Authentication
Authentication is controlled via CAS URL pattern matching (see below for the patterns).

#### Secured URL Patterns

Due to limitations of the ala-cas-client library used to intercept HTTP requests and redirect where necessary to the login page, we cannot use true RESTful URL patterns and rely on the HTTP verb. Therefore, create, update and delete URLs all end with the verb. If in future the ala-cas-client library is updated to handle the HTTP verb, these URLs can simply be modified to remove the verb: no other changes should be necessary. All URLs are mapped with the appropriate HTTP verb as well as having the verb in the URL itself.

See [URLMappings.groovy](https://github.com/AtlasOfLivingAustralia/profile-hub/blob/master/grails-app/conf/UrlMappings.groovy) for URL patterns.

This is the URL pattern matching regex list to be used for CAS authentication in the profile hub:

```
/.*/update.*, /.*/create.*, /.*/delete.*, /user/.*, /audit/.*, /admin/*
```

### Authorisation
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

#### Roles
There are 5 roles: ALA\_ADMIN, ADMIN, EDITOR, REVIEWER, USER. The level of access is as follows: ALA\_ADMIN > ADMIN > EDITOR > REVIEWER > USER. I.e. an ALA\_ADMIN can do everything an ADMIN can do, and an ADMIN can do everything that and EDITOR can do, etc.

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
|Export to PDF/JSON|USER| |
|Create new Publication|ADMIN| |
|Add comments to profile|REVIEWER| |

Roles are currently defined per-opus and only have permissions for that particular opus.



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
   1. ```npm install karma-ng-html2js-preprocessor --save-dev```
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

# Name Matching

Naming matching is a complicated process. It is triggered under three scenarios:

1. When creating a new profile
1. When editing the name of an existing profile
1. During a bulk import of an existing dataset

The first two scenarios behave in exactly the same manner. The third is a bit different because it is an automatic process (no human involvement).

There are two sources of name matching:

1. The ALA Name index
1. The National Species List (NSL)

### Matching ALA Names

Names are matched against the ALA Name Index for two purposes:

1. The ALA Name Index caters for complex matching rules which are not currently supported by the NSL.
1. Matching to an ALA name allows the profile to access information from other ALA systems, such as images and occurrence information.

The ultimate source of names in the ALA Name Index is the NSL. Therefore, baring synchronisation delays, there should be no discrepancies between ALA-matched and NSL-matched names.

### Matching NSL Names

Names are matched against the NSL to allow access to the APNI name concepts (which the user can select from on the Profile Edit page).

## Name matching when creating/editing a profile

1. The user enters the name, with or without the authority (e.g. "Acacia dealbata" or "Acacia dealbata Link"
   1. The user clicks the Check Name button
1. The system attempts to match the name using the ALA Name Matching API
   1. This API follows complex matching rules, including following synonyms, looking at different levels of the taxonomic hierarchy, and so on. The details of this process is not covered here.
   1. If there is a single _exact_ match, then the system will present the user with options to
      1. Proceed, whereby the profile will be matched to the name, and the profile name and the matched name will be the same; or
      1. Manually select a matching name, whereby the profile name will be different to the matched name
   1. If there is single _non-exact_ match, then the system will present the user with options to
      1. Use the matched name instead, which will change the profile name to the matched name; or
      1. Proceed with the name as they entered it, whereby the profile name will be different to the matched name; or
      1. Manually select a matching name, whereby the profile name will be different to the matched name
   1. If there is no matching name, or more than matching name, then the system will present the user with options to
      1. Proceed with the name as they entered it, whereby the profile will NOT be matched to any name; or
      1. Manually select a matching name, whereby the profile name will be different to the matched name
1. Once the first step is complete, the system will attempt to match the name against the NSL
   1. If there is a single match, the profile will be automatically matched to that NSL name
      1. If the previous step, for any reason, did not identify the name authority, and the matched NSL name includes the authority, then the profile will use the authority from the NSL. This should rarely occur
   1. If there is no match, or multiple matches, then the profile will not be matched to any NSL name

The user is also able to remove the matched name on the edit profile screen. This is useful for cases where the system automatically matched to the wrong name during a bulk import (see below for more information on bulk imports).

### Nomenclature (aka concept)

The user is able to select the appropriate nomenclature/concept from a list of available concepts (sourced from the NSL), if the name was matched to an NSL name.

## Name matching during a bulk import

1. For each profile in the data set, the system will match the name using the ALA Name Matching API.
   1. If there is a single match, regardless of whether it was an exact match or not, then the system will match the profile to that name
   1. If there was no match, or multiple matches, then the system will not match the name
1. If the source data includes an NSL name identifier, then the system will match the profile to that NSL name
1. If the source data does not include an NSL name identifier, then the system will attempt to match the name against the NSL
   1. If there is a single match, the profile will be matched to that NSL name
      1. If the previous step, for any reason, did not identify the name authority, and the matched NSL name includes the authority, then the profile will use the authority from the NSL. This should rarely occur
   1. If there is no match, or multiple matches, then the profile will not be matched to any NSL name

### Nomenclature (aka concept)

There are several options for the selection of the nomenclature/concept during the import process:

1. The source data can specify the NSL identifier of the nomenclature to use
1. The import process can indicate that the system should attempt to match the profile to an appropriate nomenclature using one of the following rules (assuming the name has matched to the NSL):
   1. APC or Latest - if there is an APC concept, use it; otherwise, use the most recent concept
   1. Latest - use the most recent concept
   1. Containing Text - try to find the most recent concept where the name contains certain text (e.g. find the concept with "Flora of NSW" or "Flora of New South Whales")
   1. NSL Search - if the source data includes a concept reference, attempt to find that reference in the NSL using the [Find Concept NSL Service](https://biodiversity.org.au/nsl/docs/main.html#find-concept)

It is important to note that any automated matching process during the bulk import will result in some level of inaccuracy.

## Mismatched Names Report

The user interface provides a "Mismatched Names Report" for a collection (available to editors and administrators). This report will list any profile where:

1. There is no matched name
1. The matched name is different to the profile name
1. There is no matched NSL name