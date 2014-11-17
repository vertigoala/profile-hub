class UrlMappings {

	static mappings = {

        name editProfile: "/profile/edit/$uuid"(controller: 'profile', action: 'edit')
        name viewProfile: "/profile/$uuid"(controller: 'profile', action: 'show')

        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
