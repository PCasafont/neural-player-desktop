package neuralplayer.desktop.controller

import neuralplayer.desktop.controller.rest.UserRestController
import neuralplayer.desktop.controller.rest.dto.UserDto
import neuralplayer.desktop.view.AuthView
import tornadofx.*

/**
 * @author Pere
 * @since 2018/05/03
 */
class UserController : Controller() {

	val userRestController: UserRestController by inject()
	val trackController: TrackController by inject()
	val playlistController: PlaylistController by inject()
	val playerController: PlayerController by inject()

	var loggedIn = false
		private set

	@Synchronized
	fun init() {
		if (loggedIn) {
			return
		}

		try {
			userRestController.getAll()
			trackController.init()
			playlistController.init()
			playerController.init()
			loggedIn = true
		} catch (e: Exception) {
			find(AuthView::class).openModal()
		}
	}

	fun create(userDto: UserDto) {
		userRestController.create(userDto)
	}
}
