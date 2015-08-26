package au.org.ala.profile.filter

import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.profile.security.PrivateCollectionSecurityExempt
import au.org.ala.web.AuthService
import org.apache.http.HttpStatus

class AccessControlFilters {

    AuthService authService
    ProfileService profileService

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                params.currentUser = authService.getDisplayName()
                List<String> usersRoles = request.userPrincipal ? request.userPrincipal.attributes.authority.split(",") : []
                params.isALAAdmin = usersRoles.contains(Role.ROLE_ADMIN.toString())

                if (grailsApplication.config.security.authorisation.disable != "true") {
                    boolean authorised = false

                    String actionFullName = "${controllerName.capitalize()}Controller.${actionName}"

                    try {
                        log.debug "Checking security for ${actionFullName}"

                        def controllerClass = grailsApplication.controllerClasses.find {
                            it.shortName.equalsIgnoreCase("${controllerName.capitalize()}Controller")
                        }

                        if (actionName) {
                            def controllerAction = controllerClass.getClazz().declaredMethods.find {
                                it.toString().indexOf(actionName) > -1
                            }

                            if (!usersRoles.contains(Role.ROLE_ADMIN.toString())) {
                                // If we have a request for a single opus, check if the user is associated with that opus.
                                // This needs to be done regardless of whether the action is secured because the outcome is
                                // used to determine what controls need to be displayed to the user (e.g. edit buttons) when
                                // rendering the (unsecured) views.
                                def opus = null
                                if (params.opusId && !params.opusId.contains(",")) {
                                    opus = profileService.getOpus(params.opusId)

                                    params.isOpusAdmin = opus.authorities?.find {
                                        it.userId == request.userPrincipal?.attributes?.userid && it.role == Role.ROLE_PROFILE_ADMIN.toString()
                                    } != null

                                    params.isOpusEditor = opus.authorities?.find {
                                        it.userId == request.userPrincipal?.attributes?.userid && it.role == Role.ROLE_PROFILE_EDITOR.toString()
                                    } != null || params.isOpusAdmin

                                    params.isOpusReviewer = opus.authorities?.find {
                                        it.userId == request.userPrincipal?.attributes?.userid && it.role == Role.ROLE_PROFILE_REVIEWER.toString()
                                    } != null || params.isOpusAdmin || params.isOpusEditor

                                    params.isOpusUser = opus.authorities?.find {
                                        it.userId == request.userPrincipal?.attributes?.userid && it.role == Role.ROLE_USER.toString()
                                    } != null || params.isOpusReviewer || params.isOpusAdmin || params.isOpusEditor
                                }
                                log.debug("User ${request.userPrincipal?.name} is : Opus Admin? ${params.isOpusAdmin}; Opus editor? ${params.isOpusEditor}; Opus reviewer? ${params.isOpusReviewer}; Opus user? ${params.isOpusUserr};")

                                if (!opus || !opus.privateCollection || params.isOpusUser || controllerAction.isAnnotationPresent(PrivateCollectionSecurityExempt)) {
                                    if (controllerAction.isAnnotationPresent(Secured)) {
                                        def annotation = controllerAction.getAnnotation(Secured)

                                        String requiredRole = annotation.role().toString()

                                        if (annotation.opusSpecific()) {
                                            if (opus) {
                                                if (requiredRole == Role.ROLE_PROFILE_ADMIN.toString()) {
                                                    authorised = params.isOpusAdmin
                                                    log.debug "Action ${actionFullName} requires ROLE_PROFILE_ADMIN. User ${request.userPrincipal?.name} has it? ${authorised}"
                                                } else if (requiredRole == Role.ROLE_PROFILE_EDITOR.toString()) {
                                                    authorised = params.isOpusAdmin || params.isOpusEditor
                                                    log.debug "Action ${actionFullName} requires ${requiredRole}. User ${request.userPrincipal?.name} has it? ${authorised}"
                                                } else if (requiredRole == Role.ROLE_PROFILE_REVIEWER.toString()) {
                                                    authorised = params.isOpusAdmin || params.isOpusEditor || params.isOpusReviewer
                                                    log.debug "Action ${actionFullName} requires ${requiredRole}. User ${request.userPrincipal?.name} has it? ${authorised}"
                                                }
                                            } else {
                                                log.debug "Security for action ${actionFullName} is opus specific, but no matching opus was found with id ${params.opusId}"
                                            }
                                        }
                                    } else {
                                        log.debug "Action ${actionFullName} is not secured"
                                        authorised = true
                                    }
                                } else {
                                    log.debug "The collection is private and the user is not registered with the collection."
                                }
                            } else {
                                params.isOpusAdmin = true
                                params.isOpusEditor = true
                                params.isOpusReviewer = true
                                params.isOpusUser = true
                                log.debug "User ${request.userPrincipal?.name} is an ALA Admin user"
                                authorised = true
                            }
                        }
                    } catch (e) {
                        log.error("Authorisation check failed", e)
                    }

                    if (!authorised) {
                        log.debug "User ${request.userPrincipal?.name} is not authorised to access action ${actionFullName}"
                        response.status = HttpStatus.SC_FORBIDDEN
                        response.sendError(HttpStatus.SC_FORBIDDEN)
                    }
                } else {
                    boolean authenticated = authService.getDisplayName() != null
                    params.isOpusAdmin = authenticated
                    params.isOpusEditor = authenticated
                    params.isOpusReviewer = authenticated
                    params.isOpusUser = authenticated
                    log.warn "**** Authorisation has been disabled! ****"
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }

}
