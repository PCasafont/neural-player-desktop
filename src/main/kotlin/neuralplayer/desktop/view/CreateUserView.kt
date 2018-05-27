package neuralplayer.desktop.view

import javafx.beans.property.SimpleStringProperty
import neuralplayer.desktop.controller.UserController
import neuralplayer.desktop.controller.rest.dto.UserDto
import tornadofx.*

class CreateUserView : View("Create User") {

	private val userController: UserController by inject()

	override val root = form {
		val username = SimpleStringProperty()
		val email = SimpleStringProperty()
		val displayName = SimpleStringProperty()
		val password = SimpleStringProperty()
		val password2 = SimpleStringProperty()
		fieldset("Create user") {
			field("Username") {
				textfield(username)
			}
			field("E-mail") {
				textfield(email)
			}
			field("Display name") {
				textfield(displayName)
			}
			field("Password") {
				passwordfield(password)
			}
			field("Repeat Password") {
				passwordfield(password2)
			}
			button("Create") {
				action {
					if (password.get() != password2.get()) {
						error("The passwords don't match!")
					}

					val userDto = UserDto(null, username.get(), email.get(), password.get(), displayName.get())
					userController.create(userDto)
					close()
				}
			}
		}
	}
}
