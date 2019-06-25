./grailsw clean-all
./grailsw refresh-dependencies
travis_retry ./grailsw prod maven-deploy --repository=$MAVEN_REPO --non-interactive

