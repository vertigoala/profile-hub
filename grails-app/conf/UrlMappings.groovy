class UrlMappings {

	static mappings = {

        name deleteAttribute: "/profile/deleteAttribute/$uuid"(controller: 'profile', action: 'deleteAttribute')
        name updateAttribute: "/profile/updateAttribute/$uuid"(controller: 'profile', action: 'updateAttribute')
        name createAttribute: "/profile/updateAttribute/"(controller: 'profile', action: 'updateAttribute')


        name editProfile: "/profile/edit/$uuid"(controller: 'profile', action: 'edit')
        name viewProfile: "/profile/$uuid"(controller: 'profile', action: 'show')
        name viewOpus: "/opus/$uuid"(controller: 'opus', action: 'show')
        name editOpus: "/opus/edit/$uuid"(controller: 'opus', action: 'edit')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(controller: 'opus', action: 'index')

        "500"(view:'/error')
	}
}
