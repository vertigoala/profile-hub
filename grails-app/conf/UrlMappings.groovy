class UrlMappings {

	static mappings = {

        name deleteAttribute: "/profile/deleteAttribute/$uuid"(controller: 'profile', action: 'deleteAttribute')
        name updateAttribute: "/profile/updateAttribute/$uuid"(controller: 'profile', action: 'updateAttribute')
        name createAttribute: "/profile/updateAttribute/"(controller: 'profile', action: 'updateAttribute')

        name updateLinks: "/profile/updateLinks/$uuid"(controller: 'profile', action: 'updateLinks')
        name updateBHLLinks: "/profile/updateBHLLinks/$uuid"(controller: 'profile', action: 'updateBHLLinks')

        name editProfile: "/profile/edit/$uuid"(controller: 'profile', action: 'edit')
        name viewProfile: "/profile/$uuid"(controller: 'profile', action: 'show')


        name findUser: "/opus/findUser"(controller: 'opus', action: 'findUser')
        name uploadTaxaToOpus: "/opus/uploadTaxa"(controller: 'opus', action: 'taxaUpload')

        name viewOpus: "/opus/$uuid"(controller: 'opus', action: 'show')
        name editOpus: "/opus/edit/$uuid"(controller: 'opus', action: 'edit')

        name bhl: "/bhl/$pageId"(controller: 'BHL', action: 'pageLookup')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'opus', action: 'index')

        "500"(view:'/error')
	}
}
