class UrlMappings {

    static mappings = {

        "/speciesList/" controller: "speciesList", action: [GET: "getAllLists"]

        "/user/search" controller: "user", action: [GET: "findUser"]

        "/opus/create" controller: "opus", action: [GET: "create", PUT: "createOpus"]

        "/opus/list" controller: "opus", action: [GET: "list"]

        "/profile/search" controller: "profile", action: [GET: "search"]

        "/opus/$opusId/profile/create" controller: "profile", action: [PUT: "createProfile"]
        "/opus/$opusId/profile/$profileId/delete" controller: "profile", action: [DELETE: "deleteProfile"]
        "/opus/$opusId/profile/$profileId/update" controller: "profile", action: [GET: "edit", POST: "updateProfile"]
        "/opus/$opusId/profile/$profileId/json" controller: "profile", action: [GET: "getJson"]
        "/opus/$opusId/profile/$profileId/pdf" controller: "export", action: [GET: "getPdf"]
        "/opus/$opusId/profile/$profileId/images" controller: "profile", action: [GET: "retrieveImages"]
        "/opus/$opusId/profile/$profileId/lists" controller: "speciesList", action: [GET: "retrieveLists"]
        "/opus/$opusId/profile/$profileId/classifications" controller: "profile", action: [GET: "retrieveClassifications"]
        "/opus/$opusId/profile/$profileId/publication" controller: "profile", action: [GET: "retrievePublication"]
        "/opus/$opusId/profile/$profileId/publication/create" controller: "profile", action: [PUT: "savePublication"]
        "/opus/$opusId/profile/$profileId/publication/$publicationId/update" controller: "profile", action: [POST: "savePublication"]
        "/opus/$opusId/profile/$profileId/publication/$publicationId/delete" controller: "profile", action: [DELETE: "deletePublication"]
        "/opus/$opusId/profile/$profileId/speciesProfile" controller: "profile", action: [GET: "retrieveSpeciesProfile"]
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

        "/opus/$opusId/vocab/$vocabId/update" controller: "vocab", action: [POST: "update"]
        "/opus/$opusId/vocab/$vocabId/findUsages" controller: "vocab", action: [GET: "findUsagesOfTerm"]
        "/opus/$opusId/vocab/$vocabId/replaceUsages" controller: "vocab", action: [POST: "replaceUsagesOfTerm"]
        "/opus/$opusId/vocab/$vocabId" controller: "vocab", action: [GET: "show"]

        "/opus/$opusId/glossary/json" controller: "glossary", action: [GET: "getGlossary"]
        "/opus/$opusId/glossary/upload" controller: "glossary", action: [POST: "upload"]
        "/opus/$opusId/glossary/item/create" controller: "glossary", action: [PUT: "saveItem"]
        "/opus/$opusId/glossary/item/$glossaryItemId/update" controller: "glossary", action: [POST: "updateItem"]
        "/opus/$opusId/glossary/item/create" controller: "glossary", action: [PUT: "saveItem"]
        "/opus/$opusId/glossary/item/$glossaryItemId/delete" controller: "glossary", action: [DELETE: "deleteItem"]
        "/opus/$opusId/glossary" controller: "glossary", action: [GET: "view"]

        "/opus/$opusId/users/update" controller: "opus", action: [POST: "updateUsers"]

        "/opus/$opusId/update" controller: "opus", action: [GET: "edit", POST: "updateOpus"]
        "/opus/$opusId/json" controller: "opus", action: [GET: "getJson"]
        "/opus/$opusId/delete" controller: "opus", action: [DELETE: "deleteOpus"]
        "/opus/$opusId" controller: "opus", action: [GET: "show"]
        "/opus" controller: "opus", action: [GET: "index"]

        "/dataResource/$dataResourceUid" controller: "collectory", action: [GET: "getResource"]
        "/dataResource/" controller: "collectory", action: [GET: "list"]

        "/bhl/$pageId"(controller: "BHL", action: "pageLookup")

        "/specimen/$specimenId"(controller: "biocache", action: "lookupSpecimen")

        "/audit/object/$id" controller: "audit", action: [GET: "object"]
        "/audit/user/$id" controller: "audit", action: [GET: "user"]

        "/" controller: "opus", action: [GET: "index"]

        "/logout/logout" controller: "logout", action: "logout"

        "500"(view: "/error")
        "404"(view: "/notFound")
        "403"(view: "/notAuthorised")
        "401"(view: "/notAuthorised")
        "/notAuthorised"(view: "/notAuthorised")
        "/error"(view: "/error")
        "/notFound"(view: "/notFound")

    }
}
