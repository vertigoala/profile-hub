DIR_NAME=$(dirname "$0")
MAVEN_REPO=$1
. $DIR_NAME/travis_retry.sh
./grailsw clean-all
./grailsw refresh-dependencies
travis_retry ./grailsw prod maven-deploy --repository=$MAVEN_REPO --non-interactive

