class UrlMappings {

	static mappings = {

        name deleteAttribute: "/profile/deleteAttribute/$attributeId"(controller: "profile", action: "deleteAttribute")
        name updateAttribute: "/profile/updateAttribute/$profileId"(controller: "profile", action: "updateAttribute")
        name createAttribute: "/profile/updateAttribute/"(controller: "profile", action: "updateAttribute")

        name updateLinks: "/profile/updateLinks/$profileId"(controller: "profile", action: "updateLinks")
        name updateBHLLinks: "/profile/updateBHLLinks/$profileId"(controller: "profile", action: "updateBHLLinks")

        name editProfile: "/profile/edit/$profileId"(controller: "profile", action: "edit")
        name viewProfile: "/profile/$profileId"(controller: "profile", action: "show")
        name getProfile: "/profile/json/$profileId"(controller: "profile", action: "getJson")

        name getImages: "/profile/images"(controller: "profile", action: "retrieveImages")
        name getLists: "/profile/lists"(controller: "profile", action: "retrieveLists")

        name findVocab: "/vocab/$vocabId" (controller: "vocab", action: "show")

        name findUser: "/opus/findUser"(controller: "opus", action: "findUser")
        name uploadTaxaToOpus: "/opus/uploadTaxa"(controller: "opus", action: "taxaUpload")

        name viewOpus: "/opus/$opusId"(controller: "opus", action: "show")
        name editOpus: "/opus/edit/$opusId"(controller: "opus", action: "edit")

        name bhl: "/bhl/$pageId"(controller: "BHL", action: "pageLookup")

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: "opus", action: "index")

        "500"(view:"/error")
        "404"(view:"/notFound")
	}
}
