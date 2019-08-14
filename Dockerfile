#
# profile-hub containerized
#
FROM tomcat:8.5-jre8-alpine

#ARG ARTIFACT_URL=https://nexus.ala.org.au/service/local/repositories/releases/content/au/org/ala/profile-hub/2.4/profile-hub-2.4.war
ARG ARTIFACT_URL=https://ala-rnp.s3.amazonaws.com/ala-assets/brasil/profile-hub-2.5-SNAPSHOT.war
ARG WAR_NAME=profile-hub

RUN mkdir -p /data/profile-hub/config \
            /data/profile-hub/temp \
            /data/profile-hub/staged-images \
            /data/profile-hub/private-images  \
            /data/lucene

RUN apk add --update curl zip tini
RUN wget $ARTIFACT_URL -q -O /tmp/$WAR_NAME && \
    mkdir -p $CATALINA_HOME/webapps/$WAR_NAME && \
    unzip /tmp/$WAR_NAME -d $CATALINA_HOME/webapps/$WAR_NAME && \
    rm /tmp/$WAR_NAME

# Tomcat configs
COPY ./tomcat-conf/* /usr/local/tomcat/conf/
COPY ./config/help-mappings.json /data/profile-hub/config/help-mappings.json
EXPOSE 8080

# NON-ROOT
RUN addgroup -g 101 tomcat && \
    adduser -G tomcat -u 101 -S tomcat && \
    chown -R tomcat:tomcat /usr/local/tomcat && \
    chown -R tomcat:tomcat /data

USER tomcat

ENV CATALINA_OPTS '-Dgrails.env=production'
ENV JAVA_OPTS '-Dport.shutdown=8005 -Dport.http=8080'

ENTRYPOINT ["tini", "--"]
CMD ["catalina.sh", "run"]
