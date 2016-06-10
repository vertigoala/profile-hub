# Profile Hub
Master: [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

Dev: [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=dev)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

# Design notes and setup instructions

See the [project wiki](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/).

# Project set up

See the [project setup page](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/Project-Setup) on the wiki.

# Name Matching

This content has been moved to the [project wiki](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/Name-Matching).





# Sandbox integration

## Requirements

1. As a taxonomist, I want to be able to upload occurrence data that is not ready for public consumption so that I can access that data while creating species treatments/profiles prior to 'publication'.
1. As the owner of a private profiles collection, I want to be able to upload occurrence data for use within the collection without making that data publically accessible, so that I can create profiles using sensitive (scientific, cultural, etc) data for use by authorised users.  
1. As the owner of 'private' occurrence data, I want to be able to 'publish' that data to the ALA directly from the Profiles application so that I can make my work publically accessible without having to go through the upload process again.
  
## Approach

The Sandbox application allows data to be uploaded and manipulated separately to the Biocache instance(s). Sandbox is essentially a simple upload interface to the biocache - i.e. the Sanbox installation actually installs an instance of the Biocache (biocache-service, ala-hub, solr, cassandra, biocache-cli). 

Using the Sandbox and allowing Profiles to extract data from either Biocache or Sandbox would achieve requirement #1.

However, neither the Sandbox nor Biocache support authorisation around data sets: once uploaded, anyone can view the data. This does not satisfy requirement #2.

Therefore, we install the backend portion of the 'Sandbox' environment with Profiles, but do not expose any of the services. All requests to the underlying biocache service are made via the Profiles application, which will ensure that only data owned by the Collection's Data Resource is ever retrieved.

This is not a terribly robust solution, as it relies on the Sandbox code staying the same as it was when this was implemented. However, this approach gives a much nicer user experience. It would probably be better still to convert Sandbox to a plugin and add it to Profile Hub.

## Implementation

This solution uses Web Components to embed portions of the sandbox data upload page into a Profiles view. This requires the ID of the appropriate DIVs in the Sandbox page to remain constant.

The sandbox upload form's ajax calls are all relative URLs, which is good for us as that means that they will all go to the context-root (profile-hub) rather than directly to the sandbox. This lets us proxy all those requests via the SandboxProxyController in Profile Hub. The one exception is the upload service call, which goes to SandboxProxyController but then directly to the biocache-service instance rather than to the Sandbox - this is because the Sandbox expects CAS to have authenticated the request, which doesn't happen when proxying via the Profile Hub.

