#!/bin/bash
#
# sync remote workspace into local path (remote IDE sessions liek VS Code)
#
while true
do 
  rsync -rlptzv --progress --delete --exclude=.git "ala-helper:/root/projetos/profile-hub/*" .
  sleep 2
done

