package au.org.ala.profile.hub

class ExportController extends BaseController {

    ProfileService profileService

    def getPdf() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {
            def model = profileService.getProfile(params.profileId as String)

            if (!model) {
                notFound()
            } else {
                model.speciesProfile = profileService.getSpeciesProfile(model.profile.guid)?.resp
                model.classifications = profileService.getClassification(model.profile.guid, model.opus.uuid)?.resp

                String occurrenceQuery = createOccurrenceQuery(model.profile, model.opus)
                model.mapImageUrl = createMapImageUrl(model.opus, occurrenceQuery)

                println model.mapImageUrl
                renderPdf(template: "/pdf/profile", model: model, filename: "${model.profile.scientificName.replaceAll(/\W/, '')}.pdf")
            }
        }
    }

    def createOccurrenceQuery = { profile, opus ->
        String occurrenceQuery;

        if (profile.guid && profile.guid != "null") {
            occurrenceQuery = "lsid:" + profile.guid;
        } else {
            occurrenceQuery = profile.scientificName;
        }

        if (opus.recordSources) {
            occurrenceQuery += " AND (data_resource_uid:" + opus.recordSources.join(" OR data_resource_uid:") + ")"
        }

        occurrenceQuery.encodeAsURL()
    }

    def createMapImageUrl = { opus, occurrenceQuery ->
        Map p = [
            baselayer: opus.mapBaseLayer,
            extents: "96.173828125,-47.11468820158343,169.826171875,-2.5694811631203973",
            outlineColour: 0x000000,
            dpi: 300,
            scale: "on",
            baselayer: "world", // opus.mapBaseLayer,
            fileName: "occurrencemap.jpg",
            format: "jpg",
            outline: true,
            popacity: 1,
            pradiuspx: 5,
            pcolour: "00ff85"
        ]

        String url = "${grailsApplication.config.biocache.base.url}${grailsApplication.config.biocache.wms.image.path}${occurrenceQuery}&"
        p.each { k, v -> url += "${k}=${v}&" }
        if (url.charAt(url.length() - 1) == "&") {
            url = url.substring(0, url.length() - 1)
        }
        url
    }

}


