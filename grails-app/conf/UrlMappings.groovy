class UrlMappings {

    static mappings = {

        "/speciesList/" controller: "speciesList", action: [GET: "getAllLists"]

        "/user/search" controller: "user", action: [GET: "findUser"]

        "/opus/create" controller: "opus", action: [GET: "create", PUT: "createOpus"]

        "/opus/list" controller: "opus", action: [GET: "list"]

        "/profile/search" controller: "search", action: [GET: "findByScientificName"]
        "/profile/search/taxon/name" controller: "search", action: "findByNameAndTaxonLevel"
        "/profile/search/taxon/level" controller: "search", action: "groupByTaxonLevel"
        "/profile/search/taxon/levels" controller: "search", action: "getTaxonLevels"

        "/opus/$opusId/profile/create" controller: "profile", action: [PUT: "createProfile"]
        "/opus/$opusId/profile/$profileId/delete" controller: "profile", action: [DELETE: "deleteProfile"]
        "/opus/$opusId/profile/$profileId/update" controller: "profile", action: [GET: "edit", POST: "updateProfile"]
        "/opus/$opusId/profile/$profileId/rename" controller: "profile", action: [POST: "renameProfile"]
        "/opus/$opusId/profile/$profileId/archive" controller: "profile", action: [POST: "archiveProfile"]
        "/opus/$opusId/profile/$profileId/restore" controller: "profile", action: [POST: "restoreArchivedProfile"]
        "/opus/$opusId/profile/$profileId/toggleDraftMode" controller: "profile", action: [POST: "toggleDraftMode"]
        "/opus/$opusId/profile/$profileId/discardDraftChanges" controller: "profile", action: [POST: "discardDraftChanges"]
        "/opus/$opusId/profile/$profileId/json" controller: "profile", action: [GET: "getJson"]
        "/opus/$opusId/profile/$profileId/pdf" controller: "export", action: [GET: "getPdf"]
        "/opus/$opusId/profile/$profileId/images" controller: "profile", action: [GET: "retrieveImages"]
        "/opus/$opusId/profile/$profileId/stagedImage/$imageId" controller: "profile", action: [GET: "getStagedImage"]
        "/opus/$opusId/profile/$profileId/stagedImage/$imageId/delete" controller: "profile", action: [DELETE: "deleteStagedImage"]
        "/opus/$opusId/profile/$profileId/file/$fileId" controller: "profile", action: [GET: "downloadTempFile"]
        "/opus/$opusId/profile/$profileId/image/upload" controller: "profile", action: [POST: "uploadImage"]
        "/opus/$opusId/profile/$profileId/lists" controller: "speciesList", action: [GET: "retrieveLists"]
        "/opus/$opusId/profile/$profileId/classifications" controller: "profile", action: [GET: "retrieveClassifications"]
        "/opus/$opusId/profile/$profileId/publication" controller: "profile", action: [GET: "retrievePublication"]
        "/opus/$opusId/profile/$profileId/publication/create" controller: "profile", action: [PUT: "savePublication"]
        "/opus/$opusId/profile/$profileId/publication/$publicationId/update" controller: "profile", action: [POST: "savePublication"]
        "/opus/$opusId/profile/$profileId/speciesProfile" controller: "profile", action: [GET: "retrieveSpeciesProfile"]
        "/opus/$opusId/profile/$profileId/bioStatus" controller: "profile", action: [GET: "getBioStatus"]
        "/opus/$opusId/profile/$profileId/attribute/$attributeId/update" controller: "profile", action: [POST: "updateAttribute"]
        "/opus/$opusId/profile/$profileId/attribute/create" controller: "profile", action: [PUT: "updateAttribute"]
        "/opus/$opusId/profile/$profileId/attribute/$attributeId/delete" controller: "profile", action: [DELETE: "deleteAttribute"]
        "/opus/$opusId/profile/$profileId/links/update" controller: "profile", action: [POST: "updateLinks"]
        "/opus/$opusId/profile/$profileId/bhllinks/update" controller: "profile", action: [POST: "updateBHLLinks"]
        "/opus/$opusId/profile/$profileId" controller: "profile", action: [GET: "show"]
        "/opus/$opusId/profile/$profileId/comment/" controller: "comment", action: [GET: "getComments"]
        "/opus/$opusId/profile/$profileId/comment/create" controller: "comment", action: [PUT: "addComment"]
        "/opus/$opusId/profile/$profileId/comment/$commentId/update" controller: "comment", action: [POST: "updateComment"]
        "/opus/$opusId/profile/$profileId/comment/$commentId/delete" controller: "comment", action: [DELETE: "deleteComment"]
        "/opus/$opusId/profile/$profileId/authorship/update" controller: "profile", action: [POST: "updateAuthorship"]

        "/opus/$opusId/vocab/$vocabId/update" controller: "vocab", action: [POST: "update"]
        "/opus/$opusId/vocab/$vocabId/findUsages" controller: "vocab", action: [GET: "findUsagesOfTerm"]
        "/opus/$opusId/vocab/$vocabId/replaceUsages" controller: "vocab", action: [POST: "replaceUsagesOfTerm"]
        "/opus/$opusId/vocab/$vocabId" controller: "vocab", action: [GET: "show"]

        "/opus/$opusId/about/json" controller: "opus", action: [GET: "getAboutHtml"]
        "/opus/$opusId/about/update" controller: "opus", action: [PUT: "updateAboutHtml"]
        "/opus/$opusId/about" controller: "opus", action: [GET: "about"]

        "/opus/$opusId/shareRequest/$requestingOpusId/" controller: "opus", action: [GET: "getSupportingCollectionRequest"]
        "/opus/$opusId/supportingCollections/respond/$requestingOpusId/$requestAction" controller: "opus", action: [POST: "respondToSupportingCollectionRequest"]
        "/opus/$opusId/supportingCollections/update" controller: "opus", action: [POST: "updateSupportingCollections"]

        "/opus/$opusId/glossary/json" controller: "glossary", action: [GET: "getGlossary"]
        "/opus/$opusId/glossary/upload" controller: "glossary", action: [POST: "upload"]
        "/opus/$opusId/glossary/item/create" controller: "glossary", action: [PUT: "saveItem"]
        "/opus/$opusId/glossary/item/$glossaryItemId/update" controller: "glossary", action: [POST: "updateItem"]
        "/opus/$opusId/glossary/item/create" controller: "glossary", action: [PUT: "saveItem"]
        "/opus/$opusId/glossary/item/$glossaryItemId/delete" controller: "glossary", action: [DELETE: "deleteItem"]
        "/opus/$opusId/glossary" controller: "glossary", action: [GET: "view"]

        "/opus/$opusId/users/update" controller: "opus", action: [POST: "updateUsers"]

        "/opus/$opusId/report/$reportId" controller: "report", action: [GET: "loadReport"]

        "/opus/$opusId/update" controller: "opus", action: [GET: "edit", POST: "updateOpus"]
        "/opus/$opusId/json" controller: "opus", action: [GET: "getJson"]
        "/opus/$opusId/delete" controller: "opus", action: [DELETE: "deleteOpus"]
        "/opus/$opusId" controller: "opus", action: [GET: "show"]
        "/opus" controller: "opus", action: [GET: "index"]

        "/dataResource/$dataResourceUid" controller: "collectory", action: [GET: "getResource"]
        "/dataResource/" controller: "collectory", action: [GET: "list"]
        "/licences/" controller: "collectory", action: [GET: "licences"]

        "/keybase/projects" controller: "opus", action: [GET: "retrieveKeybaseProjects"]

        "/checkName" controller: "profile", action: [GET: "checkName"]

        "/nsl/listConcepts/$nslNameIdentifier" controller: "NSL", action: [GET: "listConcepts"]

        "/bhl/$pageId"(controller: "BHL", action: "pageLookup")

        "/specimen/$specimenId"(controller: "biocache", action: "lookupSpecimen")

        "/audit/object/$id" controller: "audit", action: [GET: "object"]
        "/audit/user/$id" controller: "audit", action: [GET: "user"]

        "/publication/$pubId" controller: "profile", action: [GET: "getPublication"]
        "/publication/$pubId/json" controller: "profile", action: [GET: "getPublicationJson"]

        "/" controller: "opus", action: [GET: "index"]

        "/logout/logout" controller: "logout", action: "logout"

        "/admin/message" controller: "admin", action: [GET: "getMessage", POST: "postMessage"]
        "/admin/reloadConfig" controller: "admin", action: [POST: "reloadConfig"]
        "/admin" controller: "admin", action: [GET: "index"]

        "500"(view: "/error")
        "404"(view: "/notFound")
        "403"(view: "/notAuthorised")
        "401"(view: "/notAuthorised")
        "/notAuthorised"(view: "/notAuthorised")
        "/error"(view: "/error")
        "/notFound"(view: "/notFound")

    }
}
