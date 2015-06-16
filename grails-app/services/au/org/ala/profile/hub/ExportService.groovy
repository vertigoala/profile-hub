package au.org.ala.profile.hub

class ExportService {

    ProfileService profileService
    BiocacheService biocacheService
    WebService webService
    def wkhtmltoxService
    def grailsApplication

    Map statusRegions = [
            ""                            : "IUCN",
            "IUCN"                        : "IUCN",
            "Australia"                   : "AU",
            "Australian Capital Territory": "ACT",
            "New South Wales"             : "NSW",
            "Northern Territory"          : "NT",
            "Queensland"                  : "QLD",
            "South Australia"             : "SA",
            "Tasmania"                    : "TAS",
            "Victoria"                    : "VIC",
            "Western Australia"           : "WA"
    ]


    byte[] createPdf(Map params) {
        def model = profileService.getProfile(params.opusId as String, params.profileId as String)
        model.options = params
        model.grailsApplication = grailsApplication

        if (!model) {
            null
        } else {
            if (params.taxonomy || params.conservation) {
                model.profile.speciesProfile = profileService.getSpeciesProfile(model.profile.guid)?.resp

                model.profile.speciesProfile?.conservationStatuses?.each {
                    it.colour = getColourForStatus(it.status)
                    it.regionAbbrev = statusRegions[it.region]
                }

                model.profile.classifications = profileService.getClassification(model.opus.uuid, params.profileId, model.profile.guid)?.resp
            }

            if (params.specimens) {
                model.profile.specimens = model.profile.specimenIds?.collect {
                    def spec = biocacheService.lookupSpecimen(it)?.resp
                    [
                            institutionName: spec.processed.attribution.institutionName,
                            collectionName : spec.processed.attribution.collectionName,
                            catalogNumber  : spec.raw.occurrence.catalogNumber,
                    ]
                }
            }

            if (params.images) {
                String searchIdentifier = model.profile.guid ? "lsid:" + model.profile.guid : model.profile.scientificName
                def images = biocacheService.retrieveImages(searchIdentifier, model.opus.imageSources.join(","))?.resp

                model.profile.images = images?.occurrences?.collect {
                    [
                            excluded        : model.profile.excludedImages.contains(it.image),
                            largeImageUrl   : it.largeImageUrl,
                            dataResourceName: it.dataResourceName
                    ]
                }
            }

            if (params.nomenclature && model.profile.nslNameIdentifier) {
                model.profile.nomenclatureHtml = webService.get("${grailsApplication.config.nsl.name.url.prefix}${model.profile.nslNameIdentifier}", false)?.resp?.replaceAll("&", "&amp;")
            }

            String occurrenceQuery = createOccurrenceQuery(model.profile, model.opus)
            model.profile.mapImageUrl = createMapImageUrl(model.opus, occurrenceQuery)

            if (params.children) {
                def children = profileService.findByNameAndTaxonLevel(model.opus.uuid, model.profile.rank, model.profile.scientificName, "99999", "0", false)?.resp
                model.profile.children = children
            }

            wkhtmltoxService.makePdf(
                    view: "/pdf/_profile",
                    footer: "/pdf/_pdfFooter",
                    header: "/pdf/_pdfHeader",
                    model: model,
                    marginLeft: 15,
                    marginTop: 20,
                    marginBottom: 20,
                    marginRight: 15,
                    headerSpacing: 10,
            )
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
                extents      : "96.173828125,-47.11468820158343,169.826171875,-2.5694811631203973",
                outlineColour: 0x000000,
                dpi          : 300,
                scale        : "on",
                baselayer    : "world",
                fileName     : "occurrencemap.jpg",
                format       : "jpg",
                outline      : true,
                popacity     : 1,
                pradiuspx    : 5,
                pcolour      : "00ff85"
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
