import au.org.ala.profile.analytics.GoogleAnalyticsClientFactory

// Place your Spring DSL code here
beans = {

    // required for @Async annotation support
    xmlns task:"http://www.springframework.org/schema/task"
    task.'annotation-driven'('proxy-target-class':true, 'mode':'proxy')

    googleAnalyticsClientFactory(GoogleAnalyticsClientFactory) {
        // override with beans.googleAnalyticsClientFactory.baseUrl property
        baseUrl = 'https://ssl.google-analytics.com'
    }
    googleAnalyticsClient(googleAnalyticsClientFactory: 'getInstance') { }
}
