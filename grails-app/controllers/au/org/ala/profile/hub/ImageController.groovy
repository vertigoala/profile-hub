package au.org.ala.profile.hub

import grails.converters.JSON

/**
 * This controller exposes a subset of the interface exposed by the ALA Images Service. This allows us to use the
 * ALA Images Client plugin to render images that are served up locally rather than from the ALA Image Service (e.g.
 * draft and private images).
 *
 * All operations on this controller need to be mapped to URL patterns which are expected by the ALA Image Client.
 */
class ImageController extends BaseController {

    ImageService imageService

    def getImageInfo() {
        if (!params.imageId) {
            badRequest "imageId is a required parameter"
        } else {
            Map imageDetails = imageService.getImageDetails(params.imageId, request.contextPath)

            if (imageDetails) {
                if (params.callback) {
                    // the image client plugin uses jsonp, so it assumes the response will be a json string wrapped in the provided callback function
                    render """${params.callback}(${imageDetails as JSON})"""
                } else {
                    render imageDetails as JSON
                }
            } else {
                notFound "No image was found for id ${params.imageId}"
            }
        }
    }

    def downloadImage() {
        if (!params.imageId) {
            badRequest "imageId is a required parameter"
        } else {
            Map imageDetails = imageService.getImageDetails(params.imageId, request.contextPath, true)
            File file = imageDetails.file

            if (file) {
                response.setContentType(imageDetails.contentType ?: "image/*")
                response.setHeader("Content-disposition", "attachment;filename=${imageDetails.originalFileName}")
                response.outputStream << file.newInputStream()
                response.outputStream.flush()
            } else {
                notFound "No image was found for id ${params.imageId}"
            }
        }
    }

    def getTile() {
        if (!params.profileId || !params.imageId || !params.zoom || !params.x || !params.y || !params.type) {
            badRequest "profileId, imageId, zoom, x, y and type are required parameters"
        } else {
            File tile = imageService.getTile(params.profileId, params.imageId, params.type, params.zoom as int, params.x as int, params.y as int)

            response.outputStream << tile.newInputStream()
            response.outputStream.flush()
        }
    }
}
