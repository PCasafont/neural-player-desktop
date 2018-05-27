package neuralplayer.desktop.controller.rest.dto

import tornadofx.*
import javax.json.JsonObject

/**
 * @author Pere
 * @since 2018/04/21
 */
class WebError(var code: String? = null,
			   var message: String? = null) : JsonModel {

	override fun updateModel(json: JsonObject) {
		with(json) {
			code = string("code")
			message = string("message")
		}
	}

	override fun toJSON(json: JsonBuilder) {
		with(json) {
			add("code", code)
			add("message", message)
		}
	}
}
