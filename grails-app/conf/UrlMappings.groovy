class UrlMappings {

    static mappings = {

        "/profile/search"(controller: "profile", action: "search")

        "/profile/deleteAttribute/$attributeId"(controller: "profile", action: "deleteAttribute")
        "/profile/updateAttribute/$profileId"(controller: "profile", action: "updateAttribute")
        "/profile/updateAttribute/"(controller: "profile", action: "updateAttribute")

        "/profile/updateLinks/$profileId"(controller: "profile", action: "updateLinks")
        "/profile/updateBHLLinks/$profileId"(controller: "profile", action: "updateBHLLinks")

        name editProfile: "/profile/edit/$profileId"(controller: "profile", action: "edit")
        name viewProfile: "/profile/$profileId"(controller: "profile", action: "show")
        "/profile/json/$profileId"(controller: "profile", action: "getJson")

        "/profile/images"(controller: "profile", action: "retrieveImages")
        "/profile/lists"(controller: "profile", action: "retrieveLists")
        "/profile/classifications"(controller: "profile", action: "retrieveClassifications")
        "/profile/speciesProfile"(controller: "profile", action: "retrieveSpeciesProfile")

        "/vocab/$vocabId"(controller: "vocab", action: "show")

        "/opus/findUser"(controller: "opus", action: "findUser")
        "/opus/uploadTaxa"(controller: "opus", action: "taxaUpload")

        name viewOpus: "/opus/$opusId"(controller: "opus", action: [GET: "show", POST: "updateOpus"])
        name editOpus: "/opus/edit/$opusId"(controller: "opus", action: "edit")
        name getOpus: "/opus/json/$opusId"(controller: "opus", action: "getJson")

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
