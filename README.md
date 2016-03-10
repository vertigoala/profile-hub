# Profile Hub
Master: [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=master)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

Dev: [![Build Status](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub.svg?branch=dev)](https://travis-ci.org/AtlasOfLivingAustralia/profile-hub)

# Design notes and setup instructions

See the [project wiki](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/).

# Project set up

See the [project setup page](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/Project-Setup) on the wiki.

# Name Matching

This content has been moved to the [project wiki](https://github.com/AtlasOfLivingAustralia/profile-hub/wiki/Name-Matching).

# Image Storage

Profiles supports images from remote sources, private images and staged images. A private image is stored relative to
the collection and associated profile /data/profile-hub/private_images/collectionId/profileUUID/imageID/imageID.jpg.
The image directory also contains tile and thumbnail directories.
A staged image is an image added while the profile is locked for major revisionand is treated similarly to a private
 image until the profile is unlocked. Staged images are stored at /data/profile-hub/staged_images/collectionId/
 profileUUID/imageID/imageID.jpg.