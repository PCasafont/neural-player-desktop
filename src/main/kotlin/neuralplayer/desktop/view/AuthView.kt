package neuralplayer.desktop.view

import neuralplayer.desktop.controller.ConfigController
import neuralplayer.desktop.controller.UserController
import tornadofx.*
import kotlin.system.exitProcess

class AuthView : View("Log in") {

	private val configController: ConfigController by inject()
	private val userController: UserController by inject()

	override val root = form {
		fieldset("Log in") {
			field("Username") {
				textfield(configController.username)
			}
			field("Password") {
				passwordfield(configController.password)
			}
			button("Log in") {
				action {
					userController.init()
					close()
				}
			}
			button("Create") {
				action {
					find(CreateUserView::class).openModal()
				}
			}
		}
	}

	override fun onUndock() {
		if (!userController.loggedIn) {
			exitProcess(0)
		}
	}

	override fun onDelete() {
		if (!userController.loggedIn) {
			exitProcess(0)
		}
	}
}
