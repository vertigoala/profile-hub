package au.org.ala.profile.hub

class StatisticsController extends BaseController {

	ProfileService profileService

	def index() {
		if (!params.opusId) {
			badRequest()
		} else {
			def response = profileService.getStatistics(params.opusId)

			handle response
		}
	}
}
