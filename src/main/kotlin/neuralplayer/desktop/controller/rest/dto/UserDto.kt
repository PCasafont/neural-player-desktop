package neuralplayer.desktop.controller.rest.dto

import tornadofx.*
import javax.json.JsonObject

/**
 * @author Pere
 * @since 2018/04/15
 */
/**
 * @author Pere
 * @since 2018/04/22
 */
class UserDto(var id: Long? = null,
			  var username: String? = null,
			  var email: String? = null,
			  var password: String? = null,
			  var displayName: String? = null,
			  var roles: List<String>? = null) : JsonModel {

	override fun updateModel(json: JsonObject) {
		with(json) {
			id = long("id")
			username = string("username")
			email = string("email")
			password = string("password")
			displayName = string("displayName")
			roles = getJsonArray("roles").map { it.toString() }
		}
	}

	override fun toJSON(json: JsonBuilder) {
		with(json) {
			add("id", id)
			add("username", username)
			add("email", email)
			add("password", password)
			add("displayName", displayName)
			add("roles", roles)
		}
	}
}
