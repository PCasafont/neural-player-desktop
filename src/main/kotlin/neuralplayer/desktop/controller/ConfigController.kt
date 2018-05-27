package neuralplayer.desktop.controller

import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import neuralplayer.desktop.controller.rest.dto.WebError
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/21
 */
class ConfigController : Controller() {

	private val api: Rest by inject()
	private val userController: UserController by inject()

	val host = SimpleStringProperty(this, "host", config.string("host") ?: "localhost")
	val port = SimpleIntegerProperty(this, "port", config.int("port") ?: 8080)
	val username = SimpleStringProperty(this, "username", config.string("username") ?: "")
	val password = SimpleStringProperty(this, "password", config.string("password") ?: "")

	fun init() {
		api.baseURI = "http://${host.value}:${port.value}"
		host.addListener { _, _, newValue ->
			api.baseURI = "http://$newValue:${port.value}"
			config.set("host", newValue)
			config.save()
		}
		port.addListener { _, _, newValue ->
			api.baseURI = "http://${host.value}:$newValue"
			config.set("port", newValue)
			config.save()
		}
		api.setBasicAuth(username.value, password.value)
		username.addListener { _, _, newValue ->
			api.setBasicAuth(newValue, password.value)
			config.set("username", newValue)
			config.save()
		}
		password.addListener { _, _, newValue ->
			api.setBasicAuth(username.value, newValue)
			config.set("password", newValue)
			config.save()
		}

		api.engine.responseInterceptor = { response ->
			if (response.statusCode / 100 != 2) {
				val webError = response.one().toModel<WebError>()
				throw RuntimeException("${webError.code}: ${webError.message}")
			}
		}

		userController.init()
	}
}
