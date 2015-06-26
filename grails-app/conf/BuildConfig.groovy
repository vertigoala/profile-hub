grails.servlet.version = "3.0" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.work.dir = "target/work"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"
//grails.plugin.location.'ala-web-theme' = "../ala-web-theme"

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

    def httpmimeVersion = "4.3.3"

    repositories {
        mavenLocal()
        mavenRepo ("http://nexus.ala.org.au/content/groups/public/") {
            updatePolicy 'always'
        }
    }

    dependencies {
        compile 'net.sf.ehcache:ehcache:2.8.4'
        compile "org.apache.httpcomponents:httpmime:${httpmimeVersion}"
        runtime "org.springframework:spring-test:4.1.2.RELEASE" // required by the rendering plugin
    }

    plugins {
        compile ":mail:1.0.7"
        build ":release:3.0.1"
        build ":tomcat:7.0.55"
        compile ":cache:1.1.6"
        compile ":uploadr:0.8.2"
        compile ":cors:1.1.6"
        compile ":modernizr:2.7.1.1"
        compile ":rendering:1.0.0"
        compile ":csv:0.3.1"
        compile ":wkhtmltopdf:0.1.8"
        runtime ":resources:1.2.14"
        runtime ":ala-bootstrap3:1.1"
        runtime ":ala-auth:1.2"
    }
}
