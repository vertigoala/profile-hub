#!/bin/bash
# stolen from travis as recommended by them:
# https://twitter.com/plexus/status/499194992632811520
source /etc/profile

RED="\033[31;1m"
GREEN="\033[32;1m"
RESET="\033[0m"

travis_retry() {
  local result=0
  local count=1
  while [ $count -le 3 ]; do
    [ $result -ne 0 ] && {
      echo -e "\n${RED}The command \"$@\" failed. Retrying, $count of 3.${RESET}\n" >&2
    }
    "$@"
    result=$?
    [ $result -eq 0 ] && break
    count=$(($count + 1))
    sleep 1
  done

  [ $count -eq 3 ] && {
    echo "\n${RED}The command \"$@\" failed 3 times.${RESET}\n" >&2
  }

  return $result
}