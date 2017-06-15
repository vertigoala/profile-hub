package au.org.ala.profile.hub

class OpusTagLib {
    static namespace = 'o'
    static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static returnObjectForTags = ['cacheBuster']
    /**
     * Return a string that can be used for cache busting something to do with an opus
     * eg the layout colours.  It will also change each version of the software and,
     * for a snapshot build, the build date.
     */
    def cacheBuster = { attrs, body ->
        def opus = attrs.remove('opus')

        def version = g.meta(name: 'app.version')

        def cb = version ?: ''
        if (version?.endsWith('-SNAPSHOT')) {
            // in a dev machine, version will probably be *-SNAPSHOT and build
            // will be empty, so take care using this with run-app or similar
            def build = g.meta(name: 'app.build')
            build = build?.replaceAll('[\\/\\s:]', '') ?: ''

            cb += '.' + build
        }

        if (opus) {
            return cb + '.' + opus.lastUpdated
        } else {
            return cb
        }
    }
}
