package au.org.ala.profile.filter

import au.org.ala.profile.hub.ProfileService
import au.org.ala.profile.security.Role
import au.org.ala.profile.security.Secured
import au.org.ala.web.AuthService
import org.apache.http.HttpStatus

class AccessControlFilters {

    AuthService authService
    ProfileService profileService

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                params.currentUser = authService.getDisplayName()

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

                            List<String> usersRoles = request.userPrincipal ? request.userPrincipal.attributes.authority.split(",") : []

                            if (!usersRoles.contains(Role.ROLE_ADMIN.toString())) {
                                // If we have a request for a single opus, check if the user is an opus admin or editor
                                // This needs to be done regardless of whether the action is secured because the outcome is
                                // used to determine what controls need to be displayed to the user (e.g. edit buttons) when
                                // rendering the (unsecured) views.
                                def opus = null
                                if (params.opusId && !params.opusId.contains(",") && request.userPrincipal) {
                                    opus = profileService.getOpus(params.opusId)

                                    params.isOpusAdmin = opus.authorities.find {
                                        it.userId == request.userPrincipal.attributes.userid && it.role == Role.ROLE_PROFILE_ADMIN.toString()
                                    } != null

                                    params.isOpusEditor = opus.authorities.find {
                                        it.userId == request.userPrincipal.attributes.userid && it.role == Role.ROLE_PROFILE_EDITOR.toString()
                                    } != null || params.isOpusAdmin

                                    params.isOpusReviewer = opus.authorities.find {
                                        it.userId == request.userPrincipal.attributes.userid && it.role == Role.ROLE_PROFILE_REVIEWER.toString()
                                    } != null || params.isOpusAdmin || params.isOpusEditor
                                }
                                log.debug("Opus Admin? ${params.isOpusAdmin}; Opus editor? ${params.isOpusEditor}; Opus reviewer? ${params.isOpusReviewer};")

                                if (controllerAction.isAnnotationPresent(Secured)) {
                                    def annotation = controllerAction.getAnnotation(Secured)

                                    String requiredRole = annotation.role().toString()

                                    if (annotation.opusSpecific()) {
                                        if (opus) {
                                            if (requiredRole == Role.ROLE_PROFILE_ADMIN.toString()) {
                                                authorised = params.isOpusAdmin
                                                log.trace "Action ${actionFullName} requires ROLE_PROFILE_ADMIN. User has it? ${authorised}"
                                            } else if (requiredRole == Role.ROLE_PROFILE_EDITOR.toString()) {
                                                authorised = params.isOpusAdmin || params.isOpusEditor
                                                log.trace "Action ${actionFullName} requires ${requiredRole}. User has it? ${authorised}"
                                            } else if (requiredRole == Role.ROLE_PROFILE_REVIEWER.toString()) {
                                                authorised = params.isOpusAdmin || params.isOpusEditor || params.isOpusReviewer
                                                log.trace "Action ${actionFullName} requires ${requiredRole}. User has it? ${authorised}"
                                            }
                                        } else {
                                            log.trace "Security for action ${actionFullName} is opus specific, but no matching opus was found with id ${params.opusId}"
                                        }
                                    }
                                } else {
                                    log.trace "Action ${actionFullName} is not secured"
                                    authorised = true
                                }
                                params.isALAAdmin = false
                            } else {
                                params.isALAAdmin = true
                                params.isOpusAdmin = true
                                params.isOpusEditor = true
                                params.isOpusReviewer = true
                                log.trace "ALA Admin user"
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
                    log.warn "**** Authorisation has been disabled! ****"
                    params.isALAAdmin = true
                    params.isOpusAdmin = true
                    params.isOpusEditor = true
                    params.isOpusReviewer = true
                }
            }
            after = { Map model ->

            }
            afterView = { Exception e ->

            }
        }
    }

}
