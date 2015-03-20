class UrlMappings {

    static mappings = {

        "/profile/search"(controller: "profile", action: "search")

        "/profile/deleteAttribute/$attributeId"(controller: "profile", action: "deleteAttribute")
        "/profile/updateAttribute/$profileId"(controller: "profile", action: "updateAttribute")
        "/profile/updateAttribute/"(controller: "profile", action: "updateAttribute")

        "/profile/updateLinks/$profileId"(controller: "profile", action: "updateLinks")
        "/profile/updateBHLLinks/$profileId"(controller: "profile", action: "updateBHLLinks")

        "/profile/"(controller: "profile", action: [PUT: "createProfile"])

        name editProfile: "/profile/edit/$profileId"(controller: "profile", action: "edit")
        name viewProfile: "/profile/$profileId"(controller: "profile", action: "show")
        name getProfile: "/profile/json/$profileId"(controller: "profile", action: "getJson")
        "/profile/delete"(controller: "profile", action: [DELETE: "deleteProfile"])

        "/profile/images"(controller: "profile", action: "retrieveImages")
        "/profile/lists"(controller: "profile", action: "retrieveLists")
        "/profile/classifications"(controller: "profile", action: "retrieveClassifications")
        "/profile/speciesProfile"(controller: "profile", action: "retrieveSpeciesProfile")

        "/vocab/$vocabId"(controller: "vocab", action: [GET: "show", POST: "update"])
        "/vocab/usages/find"(controller: "vocab", action: [GET: "findUsagesOfTerm"])
        "/vocab/usages/replace"(controller: "vocab", action: [POST: "replaceUsagesOfTerm"])

        "/opus/findUser"(controller: "opus", action: "findUser")
        "/opus/uploadTaxa"(controller: "opus", action: "taxaUpload")

        "/opus/list"(controller: "opus", action: "list")
        name viewOpus: "/opus/$opusId"(controller: "opus", action: [GET: "show", POST: "updateOpus", PUT: "createOpus", DELETE: "deleteOpus"])
        name editOpus: "/opus/edit/$opusId"(controller: "opus", action: "edit")
        name getOpus: "/opus/json/$opusId"(controller: "opus", action: "getJson")
        name createOpus: "/opus/create"(controller: "opus", action: "create")
        "/opus/"(controller: "opus", action: [PUT: "createOpus"])

        "/dataResource/list"(controller: "collectory", action: "list")
        "/dataResource/$dataResourceUid"(controller: "collectory", action: "getResource")

        "/bhl/$pageId"(controller: "BHL", action: "pageLookup")


        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "opus", action: "index")

        "500"(view: "/error")
        "404"(view: "/notFound")
    }
}
