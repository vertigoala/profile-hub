#!/bin/bash
#
# sync remote workspace into local path (remote IDE sessions like VS Code)
#
while true
do 
  rsync -rlptzv --progress --delete --exclude=.git --exclude node_modules --exclude target "ala-builder:/home/ubuntu/projetos/profile-hub/*" .
  sleep 5
done
