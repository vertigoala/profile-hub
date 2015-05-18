package au.org.ala.profile.hub

class ExportController extends BaseController {

    ProfileService profileService
    BiocacheService biocacheService
    WebService webService
    def wkhtmltoxService

    Map statusRegions = [
            "": "IUCN",
            "IUCN": "IUCN",
            "Australia": "AU",
            "Australian Capital Territory": "ACT",
            "New South Wales": "NSW",
            "Northern Territory": "NT",
            "Queensland": "QLD",
            "South Australia": "SA",
            "Tasmania": "TAS",
            "Victoria": "VIC",
            "Western Australia": "WA"
    ]

    def getPdf() {
        if (!params.profileId) {
            badRequest "profileId is a required parameter"
        } else {

            def model = profileService.getProfile(params.opusId as String, params.profileId as String)
            model.options = extractOptionsFromParams()

            if (!model) {
                notFound()
            } else {
                if (params.taxonomy || params.conservation) {
                    model.speciesProfile = profileService.getSpeciesProfile(model.profile.guid)?.resp

                    model.speciesProfile.conservationStatuses?.each {
                        it.colour = getColourForStatus(it.status)
                        it.regionAbbrev = statusRegions[it.region]
                    }

                    model.classifications = profileService.getClassification(model.opus.uuid, params.profileId, model.profile.guid)?.resp
                }

                if (params.specimens) {
                    model.specimens = model.profile.specimenIds?.collect {
                        def spec = biocacheService.lookupSpecimen(it)?.resp
                        [
                                institutionName: spec.processed.attribution.institutionName,
                                collectionName: spec.processed.attribution.collectionName,
                                catalogNumber : spec.raw.occurrence.catalogNumber,
                        ]
                    }
                }

                if (params.images) {
                    String searchIdentifier = model.profile.guid ? "lsid:" + model.profile.guid : model.profile.scientificName
                    def images = biocacheService.retrieveImages(searchIdentifier, model.opus.imageSources.join(","))?.resp

                    model.images = images?.occurrences?.collect {
                        [
                                excluded     : model.profile.excludedImages.contains(it.image),
                                largeImageUrl: it.largeImageUrl,
                                dataResourceName: it.dataResourceName
                        ]
                    }
                }

                if (params.nomenclature && model.profile.nslNameIdentifier) {
                    model.nomenclatureHtml = webService.get("${grailsApplication.config.nsl.name.url.prefix}${model.profile.nslNameIdentifier}", false)?.resp?.replaceAll("&", "&amp;")
                }

                String occurrenceQuery = createOccurrenceQuery(model.profile, model.opus)
                model.mapImageUrl = createMapImageUrl(model.opus, occurrenceQuery)

                def byte[] pdfData = wkhtmltoxService.makePdf(
                        view: "/pdf/_profile",
                        model: model,
                        marginLeft: 15,
                        marginTop: 20,
                        marginBottom: 20,
                        marginRight: 15,
                        headerSpacing: 10,
                )

                response.contentType = 'application/x-pdf'
                response.setHeader 'Content-disposition', "attachment; filename=\"${model.profile.scientificName.replaceAll(/\\W/, '')}.pdf\""
                response.outputStream << pdfData
                response.outputStream.flush()
            }
        }
    }

    def extractOptionsFromParams() {
        [
                attributes: params.attributes,
                map: params.map,
                nomenclature: params.nomenclature,
                taxonomy: params.taxonomy,
                bibliography: params.bibliography,
                links: params.links,
                bhllinks: params.bhllinks,
                specimens: params.specimens,
                conservation: params.conservation,
                images: params.images
        ]
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

    def getColourForStatus(status) {
        String colour;

        if (status =~ /extinct$/ || status =~ /wild/) {
            colour = "red";
        } else if (status =~ /Critically/ || status =~ /^Endangered/ || status =~ /Vulnerable/) {
            colour = "yellow";
        } else {
            colour = "green";
        }

        return colour;
    };


}


