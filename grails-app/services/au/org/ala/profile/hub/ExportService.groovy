package au.org.ala.profile.hub

import au.org.ala.profile.hub.reports.BackCoverImageRenderer
import au.org.ala.profile.hub.reports.ColourParser
import au.org.ala.profile.hub.reports.ColourUtils
import au.org.ala.profile.hub.reports.GradientRenderer
import au.org.ala.profile.hub.reports.JasperExportFormat
import au.org.ala.profile.hub.reports.JasperReportDef
import au.org.ala.profile.hub.util.HubConstants
import au.org.ala.ws.service.WebService
import grails.converters.JSON
import grails.transaction.NotTransactional
import net.glxn.qrgen.QRCode
import net.glxn.qrgen.image.ImageType
import net.sf.jasperreports.engine.JRParameter
import net.sf.jasperreports.engine.JRSimpleTemplate
import net.sf.jasperreports.engine.JRTemplate
import net.sf.jasperreports.engine.data.JsonDataSource
import net.sf.jasperreports.engine.design.JRDesignStyle
import net.sf.jasperreports.engine.fill.JRFileVirtualizer
import net.sf.jasperreports.engine.type.ModeEnum
import net.sf.jasperreports.engine.util.SimpleFileResolver
import org.apache.commons.io.IOUtils
import org.springframework.web.context.request.RequestContextHolder
import org.codehaus.groovy.grails.web.mapping.LinkGenerator

import java.awt.Color
import java.util.concurrent.ConcurrentLinkedQueue

import static au.org.ala.profile.hub.reports.ColourUtils.contrastRatio
import static au.org.ala.profile.hub.reports.ColourUtils.darken
import static au.org.ala.profile.hub.reports.ColourUtils.lighten
import static au.org.ala.profile.hub.reports.ColourUtils.relativeLuminance
import static groovyx.gpars.GParsPool.withPool
import static org.owasp.html.Sanitizers.BLOCKS
import static org.owasp.html.Sanitizers.FORMATTING
import static org.owasp.html.Sanitizers.IMAGES
import static org.owasp.html.Sanitizers.LINKS
import static org.owasp.html.Sanitizers.STYLES
import static org.owasp.html.Sanitizers.TABLES

class ExportService {

    private static final int THREAD_COUNT = 10
    static transactional = false
    static final String LOCAL_IMAGE_THUMBNAIL_REGEX = "http.*?/image/thumbnail/(${Utils.UUID_REGEX_PATTERN}).*"
    public static final String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX"
    public static final double AUTHOR_ADJUSTMENT = 0.135
    public static final double PROTOLOGUE_ADJUSTMENT = 0.63

    ProfileService profileService
    BiocacheService biocacheService
    ImageService imageService
    WebService webService
    EmailService emailService
    NslService nslService
    KeybaseService keybaseService
    JasperService jasperService
    MapService mapService
    def grailsApplication
    LinkGenerator grailsLinkGenerator

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


    void createPdfAsych(Map params, boolean latest = false) {
        profileService.createPDFJob(params, latest)
    }

    @NotTransactional
    void createAndEmailPDF(Map params, boolean latest = false) {
        String filename = UUID.randomUUID()
        File f = new File("${grailsApplication.config.temp.file.location}/${filename}.pdf")
        f.withOutputStream { fileStream ->
            createPdf(params, fileStream, latest)
        }

        String url = "${grailsApplication.config.grails.serverURL}/opus/${params.opusId}/profile/${params.profileId}/file/${filename}.pdf"
        emailService.sendEmail(params.email, "${params.opusTitle}<no-reply@ala.org.au>", "Your file is ready to download",
                """<html><body>
                    <p>The PDF you requested from ${params.opusTitle} can now be downloaded from <a href='${url}'>this url</a>.</p>
                    <p>Please note that this file will only remain on the server for a few days.</p>
                    <p>This is an automated email. Please do not reply.</p>
                    </body></html>""")
    }

    @NotTransactional
    void createPdf(Map params, OutputStream outputStream, boolean latest = false) {
        createPdf(params, { outputStream }, latest)
    }

    /**
     * Create a PDF for the given parameters and send the generated PDF bytes to an output stream generated by the closure argument.
     * @param params The PDF parameters
     * @param outputStreamSupplier A supplier of an output stream.  Useful to delay acquiring the output stream for environments like JEE where simply getting the OutputStream can cause side effects.
     * @param latest (optional) = defaults to false
     */
    @NotTransactional
    void createPdf(Map params, Closure<OutputStream> outputStreamSupplier, boolean latest = false) {
        // Create curated report model
        Map opus = webService.get("${grailsApplication.config.profile.service.url}/opus/${URLEncoder.encode(params.opusId, "UTF-8")}")?.resp
        Map curatedModel = getCurateReportModel(opus, params)

        // Transform curated model to JSON format input stream
        InputStream inputStream = IOUtils.toInputStream((curatedModel as JSON).toString(), "UTF-8")

        // Runtime classpath directory where the reports are available
        File reportsDir = new File(grailsApplication.mainContext.getResource('classpath:reports/profiles/PROFILES.jrxml').URL.file.replaceFirst("/[\\w_]+.jrxml\$", ""))

        // Generate QR code with profile URL
        InputStream qrCodeInputStream = new ByteArrayInputStream(QRCode.from(curatedModel?.colophon?.profileLink).withSize(150, 150).to(ImageType.JPG).stream().toByteArray())

        // Generate report and return byte array

        final dataSource = new JsonDataSource(inputStream)
        dataSource.datePattern = ISO_8601_DATE_FORMAT

        JasperReportDef reportDef = new JasperReportDef(
                name: 'profiles/PROFILES.jrxml',
                fileFormat: JasperExportFormat.PDF_FORMAT,
                dataSource: dataSource,
                parameters: [
                        'PROFILES_REPORT_OPTIONS': curatedModel.options,
                        'REPORT_FILE_RESOLVER'   : new SimpleFileResolver(reportsDir),
                        'QR_CODE'                : qrCodeInputStream,
                        'GRADIENT'               : generateGradientRenderer(opus),
                        'COVER_IMAGE'            : getImageURL(opus, opus.brandingConfig?.pdfBannerUrl),
                        'BACK_COVER_IMAGE'       : generateBackCoverImageRenderer(opus)
                ]
        )
        JRFileVirtualizer virtualizer = new JRFileVirtualizer(20, System.getProperty("java.io.tmpdir"))
        reportDef.addParameter(JRParameter.REPORT_VIRTUALIZER, virtualizer)
        JRTemplate styleTemplate = generateStyleTemplate(opus)
        reportDef.addParameter(JRParameter.REPORT_TEMPLATES, [styleTemplate])

        try {
            jasperService.generateReport(reportDef, outputStreamSupplier)
        } finally {
            virtualizer.cleanup()
        }
    }

    BackCoverImageRenderer generateBackCoverImageRenderer(opus) {
        if (opus.brandingConfig?.pdfBackBannerUrl) {
            ColourParser cp = new ColourParser()
            def callToActionColour = cp.decodeColorWithDefault(opus.theme.callToActionColour, DEFAULT_CALL_TO_ACTION)
            def backCoverUrl = opus.brandingConfig?.pdfBackBannerUrl
            log.debug("Back cover URL: $backCoverUrl")
            def url = getImageURL(opus, backCoverUrl)

            return new BackCoverImageRenderer(url, ColourUtils.withAlpha(callToActionColour, 0.7))
        } else {
            return null
        }
    }
    private final static Color DEFAULT_CALL_TO_ACTION = Color.decode('#D47500')
    private final static Color DEFAULT_CALL_TO_ACTION_TEXT = Color.decode('#343434')
    private final static Color DEFAULT_MAIN_BACKGROUND = Color.WHITE
    private final static Color DEFAULT_MAIN_TEXT = Color.BLACK
    private final static Color DEFAULT_HEADER_BORDER = Color.decode('#d45500')
    private final static Color DEFAULT_HEADER_TEXT = Color.decode('#343434')
    private final static Color DEFAULT_FOOTER_BORDER = Color.decode('#d45500')
    private final static Color DEFAULT_FOOTER_TEXT = Color.WHITE
    private final static Color DEFAULT_FOOTER_BACKGROUND = Color.decode('#343434')

    GradientRenderer generateGradientRenderer(opus) {
        ColourParser cp = new ColourParser()
        def mainBackgroundColour = cp.decodeColorWithDefault(opus.theme.mainBackgroundColour, DEFAULT_MAIN_BACKGROUND)
        return new GradientRenderer(mainBackgroundColour)
    }

    JRTemplate generateStyleTemplate(Map opus) {
        ColourParser cp = new ColourParser()

        def theme = opus.theme
        def mainBackgroundColour = cp.decodeColorWithDefault(theme.mainBackgroundColour, DEFAULT_MAIN_BACKGROUND)
        def mainTextColour = cp.decodeColorWithDefault(theme.mainTextColour, DEFAULT_MAIN_TEXT)
        def callToActionColour = cp.decodeColorWithDefault(theme.callToActionColour, DEFAULT_CALL_TO_ACTION)
        def callToActionTextColour = cp.decodeColorWithDefault(theme.callToActionTextColour, DEFAULT_CALL_TO_ACTION_TEXT)
        def footerBackgroundColour = cp.decodeColorWithDefault(theme.footerBackgroundColour, DEFAULT_FOOTER_BACKGROUND)
        def footerTextColour = cp.decodeColorWithDefault(theme.footerTextColour, DEFAULT_FOOTER_TEXT)
        def footerBorderColour = cp.decodeColorWithDefault(theme.footerBorderColour, DEFAULT_FOOTER_BORDER)
        def headerTextColour = cp.decodeColorWithDefault(theme.headerTextColour, DEFAULT_HEADER_TEXT)
        def headerBorderColour = cp.decodeColorWithDefault(theme.headerBorderColour, DEFAULT_HEADER_BORDER)

        JRSimpleTemplate template = new JRSimpleTemplate()

        def defaultStyle = new JRDesignStyle()
        defaultStyle.default = true
        defaultStyle.name = "Base"
        defaultStyle.forecolor = mainTextColour
        // TODO alpha
        if (mainBackgroundColour == Color.WHITE) {
            defaultStyle.mode = ModeEnum.TRANSPARENT
        } else {
            defaultStyle.mode = ModeEnum.OPAQUE
            defaultStyle.backcolor = mainBackgroundColour
        }
        template.addStyle(defaultStyle)

        // TODO alpha
        def textLuminance = relativeLuminance(mainTextColour.red, mainTextColour.green, mainTextColour.blue)
        def backLuminance = relativeLuminance(mainBackgroundColour.red, mainBackgroundColour.green, mainBackgroundColour.blue)
        boolean backLight = textLuminance < backLuminance
        double contrast
        Color authorColor, protologueColor
        if (backLight) {
            contrast = contrastRatio(backLuminance, textLuminance)
            authorColor = lighten(mainTextColour, AUTHOR_ADJUSTMENT)
            protologueColor = lighten(mainTextColour, PROTOLOGUE_ADJUSTMENT)
        } else {
            contrast = contrastRatio(textLuminance, backLuminance)
            authorColor = darken(mainTextColour, AUTHOR_ADJUSTMENT)
            protologueColor = darken(mainTextColour, PROTOLOGUE_ADJUSTMENT)
        }
        if (contrast < 4.5) {
            log.warn("contrast of $mainBackgroundColour and $mainTextColour is only $contrast")
        }

        def authorStyle = new JRDesignStyle()
        authorStyle.name = "Author"
        authorStyle.parentStyle = defaultStyle
        authorStyle.forecolor = authorColor
        template.addStyle(authorStyle)

        def protologueStyle = new JRDesignStyle()
        protologueStyle.name = "Protologue"
        protologueStyle.parentStyle = defaultStyle
        protologueStyle.forecolor = protologueColor
        template.addStyle(protologueStyle)

        def backCover = new JRDesignStyle()
        backCover.name = "BackCover"
        backCover.mode = ModeEnum.OPAQUE
        backCover.backcolor = callToActionColour
        backCover.linePen.lineColor = callToActionColour
        backCover.lineBox.pen.lineColor = callToActionColour
        template.addStyle(backCover)

        def header = new JRDesignStyle()
        header.name = "Header"
        header.parentStyle = defaultStyle
        header.forecolor = headerTextColour
        header.linePen.lineColor = headerBorderColour
        header.lineBox.pen.lineColor = headerBorderColour
        template.addStyle(header)

        def footer = new JRDesignStyle()
        footer.name = "Footer"
        footer.forecolor = footerTextColour
        footer.mode = ModeEnum.OPAQUE
        footer.backcolor = footerBackgroundColour
        footer.linePen.lineColor = footerBorderColour
        footer.lineBox.pen.lineColor = footerBorderColour
        template.addStyle(footer)

        def callToAction = new JRDesignStyle()
        callToAction.name = "CallToAction"
        callToAction.mode = ModeEnum.OPAQUE
        callToAction.forecolor = callToActionTextColour
        callToAction.backcolor = callToActionColour
        template.addStyle(callToAction)

        def detail = new JRDesignStyle()
        detail.name = "Detail"
        detail.forecolor = callToActionColour
        template.addStyle(detail)

        def detailBox = new JRDesignStyle()
        detailBox.name = "DetailBox"
        detailBox.linePen.lineColor = headerBorderColour
        detailBox.lineBox.pen.lineColor = headerBorderColour
        template.addStyle(detailBox)

        return template
    }

    /**
     * Retrieve profile/s data in multiple threads and other required info. Put them all together in a the required data structure for the report template ingestion
     * @param opus The opus for the profiles as a map because why would you want to know what the properties were at compile time?
     * @param params Just some params, who knows what, keys could be anything, no clues as to what we're expecting.  Brilliant.
     * @param latest (optional) = defaults to false
     * @return
     */
    private Map getCurateReportModel(Map opus, Map params) {
        def curatedModel = [
                options : params,
                profiles: [] as ConcurrentLinkedQueue
        ]

        curatedModel.profiles << loadProfileData(params.profileId as String, opus, params)

        if (params.children && curatedModel.profiles[0].profile.rank) {
            def children = profileService.findByNameAndTaxonLevel(params.opusId, curatedModel.profiles[0].profile.rank, curatedModel.profiles[0].profile.scientificName, "99999", "0", "taxonomy", false)?.resp

            // By default, the RequestAttributes thread local used by Grails/Spring is not inheritable, so new threads
            // will not have access to the request context when calling web services. This line works around this issue
            // by resetting the request attributes with the inheritable flag set to true, meaning spawned threads will
            // inherit the state.
            RequestContextHolder.setRequestAttributes(RequestContextHolder.getRequestAttributes(), true)

            withPool(THREAD_COUNT) {
                children.eachParallel {
                    if (it.profileId != params.profileId && it.scientificName != params.profileId) {
                        Map model = loadProfileData(it.profileId, opus, params)
                        if (model?.profile) {
                            model.profile.taxonomicOrder = it.taxonomicOrder
                            curatedModel.profiles << model
                        }
                    }
                }
            }

            // the findByNameAndTaxonLevel operation returns the profiles in the correct taxonomic order (genera inside
            // families, species inside genera, etc), so ensure that the order is correct after the multithreaded processing
            curatedModel.profiles = curatedModel.profiles.sort { it.profile.taxonomicOrder }
        }

        def firstProfile = curatedModel.profiles[0]?.profile

        String opusUrl = grailsLinkGenerator.link(uri: "/opus/${Utils.encPath(opus.shortName?: opus.uuid)}", absolute: true)

        curatedModel.options << [
                allowFineGrainedAttribution: opus.allowFineGrainedAttribution,
                displayToc                 : curatedModel.profiles?.size() > 1,
                headerTitle                : "${opus.title}: ${firstProfile?.fullName ?: firstProfile?.scientificName}",
                footerText                 : "${opusUrl}"
        ]

        curatedModel << [
                cover   : [
                        title       : opus.title,
                        subtitle    : firstProfile?.fullName ?: firstProfile?.scientificName,
                        banner      : opus.bannerUrl,
                        headerTitleHtml: convertTagsForJasper(sanitizeHtml(opus.opusLayoutConfig.bannerOverlayText)) ?: stripTextFromNonFormattingHtmlTags(opus.title) ?: opus.uuid,
                        primaryImage: firstProfile?.primaryImage ?: (firstProfile?.images?.size() > 0 ? firstProfile?.images[0].leftImage.largeImageUrl : ''),
                        logoUrl1    : getFilePath (opus, opus.brandingConfig?.logos?.getAt(0)?.logoUrl), //Use getAt instead of index syntax [] to avoid NPE
                        logoUrl2    : getFilePath (opus, opus.brandingConfig?.logos?.getAt(1)?.logoUrl),
                        logoUrl3    : getFilePath (opus, opus.brandingConfig?.logos?.getAt(2)?.logoUrl),
                        authorship  : firstProfile.authorship?: null,
                        citation    : firstProfile.citation?:null
                ],
                colophon: [
                        collectionCopyright: "&copy; ${opus.copyrightText}",
                        genericCopyright   : HubConstants.PDF_COPYRIGHT_TEXT,
                        pdfLicense         : opus.brandingConfig.pdfLicense,
                        profileLink        : grailsLinkGenerator.link(uri: "/opus/${Utils.encPath(opus.shortName?: opus.uuid)}/profile/${Utils.encPath(firstProfile?.uuid)}", absolute: true),
                        citation           : firstProfile.citation?:null,
                        version            : firstProfile.version ?: null,
                        lastUpdated        : firstProfile.lastPublished ?: firstProfile.lastUpdated ?: null,
                        issn               : opus.brandingConfig.issn ?: null
                ]
        ]

        return curatedModel
    }

    private URL getImageURL(def opus, String logoUrl) {
        def filePath = getFilePath(opus, logoUrl)
        return filePath ? new File(filePath).toURI().toURL() : logoUrl?.toURL()
    }

    private String getFilePath (def opus, String logoUrl) {
        if (opus && logoUrl) {
            String text = "/image/"
            int i = logoUrl.indexOf(text)
            if (i > 0) {
                def filename = logoUrl.substring(i + text.length())
                File file = new File("${grailsApplication.config.image.private.dir}/${opus.uuid}/$filename")
                if (file.exists()) {
                    return "${grailsApplication.config.image.private.dir}/${opus.uuid}/$filename";
                }
            }
        }
        return "";
    }

    /**
     * Custom ETL process to retrieve and format the profile data required to render the report
     * @param profileId
     * @param opus
     * @param params
     * @return
     */
    private Map loadProfileData(String profileId, Map opus, Map params) {

        Map<String, Map> model = [:]
        model.profile = webService.get("${grailsApplication.config.profile.service.url}/opus/${opus.uuid}/profile/${URLEncoder.encode(profileId, "UTF-8")}?latest=${false}")?.resp

        if (params.taxonomy || params.conservation) {
            model.profile.speciesProfile = profileService.getSpeciesProfile(model.profile.guid)?.resp

            model.profile.speciesProfile?.conservationStatuses = model.profile.speciesProfile?.conservationStatuses?.collect {[
                        colour : getColourForStatus(it.value.status),
                        regionAbbrev: statusRegions[it.key] ?: it.key,
                        rawStatus: it.value.status
            ]}

            model.profile.classifications = model.profile.classification.collect { [rank: it.rank, scientificName: it.name] }
            if (model.profile.classifications && model.profile.taxonomyTree) {
                model.profile.classifications.add(0, [rank: "Source", scientificName: model.profile.taxonomyTree])
            }
        }

        if (params.key) {
            model.profile.bracketedKey = keybaseService.getPrintableBracketedKey(opus.keybaseProjectId, model.profile.scientificName)
        }

        if (params.specimens) {
            model.profile.specimens = model.profile.specimenIds?.collect {
                def spec = biocacheService.lookupSpecimen(it)?.resp
                [
                        institutionName: spec?.processed?.attribution?.institutionName,
                        collectionName : spec?.processed?.attribution?.collectionName,
                        catalogNumber  : spec?.raw?.occurrence?.catalogNumber,
                ]
            }
        }

        // Retrieve image and get primary image. This has to be done always, regardless of the user choosing to display the images section or not
        String searchIdentifier = model.profile.guid ? "lsid:" + model.profile.guid : model.profile.scientificName

        // incrementing number to be displayed against all images in the report (e.g. Fig 1,...)
        int figureNumber = 1

        model.profile.images = imageService.retrieveImages(opus, model.profile, searchIdentifier, true)?.resp
        List<Map> images = model.profile.images

        def replaceTitleWithOptionalCaption = { Map m -> m?.metadata?.title = m?.caption ? m?.caption : m?.metadata?.title }
        images?.each {
            replaceTitleWithOptionalCaption(it)
        }

        String descriptionAttribute = ""
        def distributionAttribute = [:]

        // Format profile attributes text
        if (params.attributes) {
            model.profile.attributes.each { attribute ->
                attribute.text = convertTagsForJasper(sanitizeHtml(formatAttributeText(attribute.text, attribute.title)))

                (attribute.title == "Description")? descriptionAttribute = attribute.text : ""

                (attribute.title == "Distribution")? distributionAttribute = attribute : null

                List<Map> attributeImages = extractImagesFromAttributeText(attribute.text, images)

                Map pairs = groupImagesIntoPairs(model.profile.scientificName, attributeImages, figureNumber)
                attribute.images = pairs.imagePairs
                figureNumber = pairs.figureNumber
            }
        }

        if (distributionAttribute) {
            model.profile.distribution = distributionAttribute
            model.profile.attributes.removeAll {
                it.title == 'Distribution'
            }
        } else {
            model.profile.distribution = [:]
        }

        model.profile.attributes.removeAll {
            it.title == 'Description'
        }

        model.profile.description = descriptionAttribute

      //  model.profile.primaryImage = images.find { it.imageId == model.profile.primaryImage }
        model.profile.primaryImage = images.find { it.primary == true }

        model.profile.images = groupImagesIntoPairs(model.profile.scientificName, images, figureNumber).imagePairs

        // Retrieve and format profile statuses
        if (params.status) {
            model.profile.status = []
            profileService.getFeatureLists(opus.uuid, model.profile.uuid)?.each { list ->
                list.items.each { item ->
                    model.profile.status << item
                }
            }
            model.profile.status.each { singleStatus ->
                singleStatus.key = formatStatusText(singleStatus.key)
                singleStatus.value = formatStatusText(singleStatus.value)
            }
        }

        // Retrieve occurrences-map image url
        String occurrenceQuery = model.profile.occurrenceQuery
        if (mapService.snapshotImageExists(opus.uuid, model.profile.uuid) && opus.mapConfig.allowSnapshots) {
            model.profile.distribution.mapImageUrl = "${grailsApplication.config.grails.serverURL}${mapService.getSnapshotImageUrlWithUUIDs("", opus.uuid, model.profile.uuid)}"
        } else {
            model.profile.distribution.mapImageUrl = mapService.constructMapImageUrl(occurrenceQuery, opus.usePrivateRecordData, "808080")
        }
        model.profile.occurrencesUrl = createOccurrencesUrl(opus, occurrenceQuery)
        model.profile.distribution.occurrencesUrl = model.profile.occurrencesUrl

        // Don't make them a String if you want the groovy truth to work (Check Bootstrap.groovy for null values workaround)
        def nslNameIdentifier = model.profile.nslNameIdentifier
        def nslNomenclatureIdentifier = model.profile.nslNomenclatureIdentifier
        // Format nomenclature references
        if (params.nomenclature && nslNameIdentifier && nslNomenclatureIdentifier) {
            model.profile.nomenclature = nslService.getConcept(nslNameIdentifier, nslNomenclatureIdentifier)

            if (model.profile.nomenclature) {
                model.profile.nomenclature.details.each { detail ->
                    detail.text = stripTextFromNonFormattingHtmlTags(detail.text)
                }
            }
        }

        if (nslNameIdentifier) {
            def nslNameDetails = nslService.getNameDetails(nslNameIdentifier)?.resp?.name?.primaryInstance?.get(0) ?: null
            String nslProtologue = nslNameDetails?.citationHtml?: null
            if (nslProtologue && nslNameDetails?.page) {
                nslProtologue += ": " + nslNameDetails.page
            }

            if (nslProtologue) {
                model.profile.nslProtologue = nslProtologue
            }
        }

        // Filter authors and contributors
        model.profile.acknowledgements = model.profile.authorship?.findAll { it.category != 'Author' }

       String profileUrl = grailsLinkGenerator.link(uri: "/opus/${Utils.encPath(opus.shortName ?: opus.uuid)}/profile/${Utils.encPath(model.profile.scientificName)}", absolute: true)
        // Generate  citation
        model.profile.citation = profileService.getCitation(opus, model.profile, profileUrl)

        model.profile.authorship = model.profile.authorship?.findAll { it.category == 'Author' }.collect {
            it.text
        }.join(", ")

        // Format scientificName
        String formattedName = formatScientificName(model.profile.scientificName, model.profile.nameAuthor, model.profile.fullName, model.profile.profileSettings)
        model.profile.fullName = formattedName

        // Format creators and editors
        model.profile.attributes.each { attribute ->
            attribute.creators = attribute?.creators && opus.allowFineGrainedAttribution ? attribute.creators.toArray().join(', ') : ''
            attribute.editors = attribute?.editors && opus.allowFineGrainedAttribution ? attribute.editors.toArray().join(', ') : ''
        }

        // Format conservation status
        if (params.conservation && model.profile.speciesProfile?.conservationStatuses) {
            model.profile.hasConservationStatus = true
            model.profile.speciesProfile.conservationStatuses = model.profile.speciesProfile?.conservationStatuses.sort {
                it.region
            }
        } else {
            model.profile.hasConservationStatus = false
        }

        // Calculate version number
        if (params.printVersion) {
            model.profile.version = model.profile.publications?.size() > 0 ? model.profile.publications.collect {
                (it.version ?: 1) as Integer
            }.max() + 1 : 1
        }

        return model
    }

/**
 *  Formatting logic mimics Util.js formatScientificName
 * @param scientificName
 * @param nameAuthor
 * @param fullName
 * @return the formatted Scientific Name for eg: <i>Acacia</i> Mill.
 */
    static String formatScientificName(String scientificName, String nameAuthor, String fullName, def profileSettings = null) {
        if (!profileSettings || profileSettings?.autoFormatName || !profileSettings?.formattedNameText) {

            if (!scientificName && !nameAuthor && !fullName) {
                return null
            }
            List<String> connectingTerms = ["subsp.", "var.", "f.", "ser.", "subg.", "sect.", "subsect."]

            if (nameAuthor) {
                connectingTerms.add(0, nameAuthor)
            }

            String name = null
            if (fullName && fullName.trim().size() > 0) {
                name = fullName
            } else if (scientificName && nameAuthor) {
                name = scientificName + " " + nameAuthor
            } else {
                name = scientificName
            }

            connectingTerms.each { connectingTerm ->
                int index = name.indexOf(connectingTerm)
                if (index > -1) {
                    String part1 = "<i>${name.substring(0, index)}</i>"
                    String part2 = connectingTerm
                    String part3 = "<i>${name.substring(index + connectingTerm.size(), name.size())}</i>"
                    name = part1 + part2 + part3
                }
            }

            return name
        } else {
            return profileSettings.formattedNameText
        }
    }


    /**
     *
     * @param text
     * @param tiltle
     * @return
     */
    static String formatAttributeText(String text, String title) {
        String replacementTitle = (title != 'Description')? "<i>${title}:</i>" : ""

        if (text) {
            if (text.startsWith('<p>')) {
                text = text.replaceFirst('<p>', "<p>${replacementTitle} ")
            } else if (text.startsWith('<')) {
                text = "<p>${replacementTitle}</p>${text}"
            } else {
                text = "${replacementTitle} ${text}"
            }

            return text
        } else {
            return ''
        }
    }

    // Applies all the default HTML sanitizers to convert text
    static String sanitizeHtml(String html) {
        if (html) {
            // Note that the FORMATTING pre defined policy allows font tags but does not allow the attributes on them,
            // so font tags are effectively stripped out of the output.
            return FORMATTING.and(STYLES).and(LINKS).and(BLOCKS).and(IMAGES).and(TABLES).sanitize(html)
        } else {
            return ''
        }

    }

    /**
     * As of JasperReports v6.2.0, HTML5 tags like strong, em and s are not supported.  This function converts
     * such tags to the older, similar and JasperReports supported b, i and strike.
     *
     * @param text The text to convert.
     * @return The converted text
     */
    static String convertTagsForJasper(String text) {
        if (text) {
            return convertTags(convertTags(convertTags(text, "strong", "b"), "em", "i"), "s", "strike")
        } else {
            return ''
        }
    }

    /**
     * Converts empty tags from one element name to another using regex
     * @param text the text to find tags in
     * @param from the tag to find
     * @param to the tag to convert to
     * @return the converted string
     */
    private static String convertTags(String text, String from, String to) {
        if (text) {
            return text.replaceAll("<$from>", "<$to>").replaceAll("</$from>", "</${to.split('\\s')[0]}>")
        } else {
            return ''
        }
    }

    /**
     * Replaces underscore with spaces and capitalize
     * @param text
     * @return
     */
    static String formatStatusText(String text) {
        text ? text.replaceAll('_', ' ').capitalize() : ''
    }

    def createOccurrencesUrl = { opus, occurrenceQuery ->
        String query
        if (opus.usePrivateRecordData) {
            query = "${grailsApplication.config.sandbox.biocache.service.url}/occurrences/search?${occurrenceQuery}"
        } else {
            query = opus.mapConfig?.biocacheUrl ? "${(opus.mapConfig.biocacheUrl as String).replaceAll('/$', '')}/occurrences/search?${occurrenceQuery}" : ""
        }
        query
    }

    def getColourForStatus(status) {
        String colour

        if (status =~ /extinct$/ || status =~ /wild/) {
            colour = "red"
        } else if (status =~ /Critically/ || status =~ /^Endangered/ || status =~ /Vulnerable/) {
            colour = "yellow"
        } else {
            colour = "green"
        }

        return colour
    };

    List<Map> extractImagesFromAttributeText(String text, List<Map> images) {
        List attributeImages = []

        List<String> imageUrls = text.findAll("<img.*/>")?.collect { it.findAll("https?[^\"]*") }?.flatten()
        imageUrls.each { url ->
            String imageId

            String remoteImageIdPrefix = "imageId"

            if (url.contains(remoteImageIdPrefix)) {
                imageId = url.find(Utils.UUID_REGEX_PATTERN)
            } else {
                // url must be in the form http://.../opus/id/profile/id/image/thumbnail/id.ext
                if (url =~ LOCAL_IMAGE_THUMBNAIL_REGEX) {
                    imageId = url.replaceAll(LOCAL_IMAGE_THUMBNAIL_REGEX, '$1')
                }
            }

            Map image = images.find { it.imageId == imageId }

            if (image) {
                attributeImages << image
            }
        }

        attributeImages
    }

    Map groupImagesIntoPairs(String profileName, List<Map> images, int figureNumber) {
        List<Map> imagePairs = []

        images.eachWithIndex { image, i ->
            if (i % 2 == 0) {
                Map left = [:]
                left.putAll(image)
                left << [
                        imageNumber    : figureNumber++,
                        scientificName : profileName,
                        imageDetailsUrl: "${grailsApplication.config.images.service.url}/image/details?imageId=${left.imageId}",
                        licenceIcon    : Utils.getCCLicenceIcon(left?.metadata?.licence)
                ]
                Map right = [:]
                right.putAll((i + 1 < images.size()) ? images[i + 1] : [:])
                if (right) {
                    right << [
                            imageNumber    : figureNumber++,
                            scientificName : profileName,
                            imageDetailsUrl: "${grailsApplication.config.images.service.url}/image/details?imageId=${right.imageId}",
                            licenceIcon    : Utils.getCCLicenceIcon(right?.metadata?.licence)
                    ]
                }
                imagePairs << ["leftImage": left, "rightImage": right]
            }
        }

        [imagePairs: imagePairs, figureNumber: figureNumber]
    }

    /**
     * Removes all XML/HTML tags that are semantic and not for text formatting
     * At the moment only <i> and <b> are supported. Adding additional ones is trivial now
     * @param html
     * @return
     */
    static String stripTextFromNonFormattingHtmlTags(String html) {
        def regex = ~/<\/?(?!i>)(?!b>)([A-Za-z0-9.'"()+,=:\s-])+>/
        return html ? html.replaceAll(regex, "") : ""
    }
}
