grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.7
grails.project.source.level = 1.7
//grails.project.war.file = "target/${appName}-${appVersion}.war"
grails.plugin.location.'ala-web-theme' = "../ala-web-theme"

grails.project.fork = [
        test: false,
        run: false
]

grails.project.dependency.resolver = "maven"
grails.project.dependency.resolution = {

    inherits("global") {}
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    def httpmimeVersion = "4.1.2"

    repositories {
        repositories {
            mavenLocal()
            mavenRepo "http://nexus.ala.org.au/content/groups/public/"
            mavenRepo "http://maven.ala.org.au/repository/"
            mavenRepo "http://repo.opengeo.org"
            mavenRepo "http://download.osgeo.org/webdav/geotools/"
        }
    }

    dependencies {
        compile 'net.sf.ehcache:ehcache:2.8.4'
        compile "org.apache.httpcomponents:httpmime:${httpmimeVersion}"
    }

    plugins {
        build ":release:3.0.1"
        build ":tomcat:7.0.55"
        compile ":scaffolding:2.1.2"
        compile ':cache:1.1.6'
        compile ':uploadr:0.8.2'
        compile ":modernizr:2.7.1.1"
        compile ":pretty-time:2.1.3.Final-1.0.1"
    }
}
