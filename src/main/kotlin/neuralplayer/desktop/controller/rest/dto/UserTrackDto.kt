package neuralplayer.desktop.controller.rest.dto

import tornadofx.*
import javax.json.JsonObject

/**
 * @author Pere
 * @since 2018/04/15
 */
data class UserTrackDto(var trackId: Long? = null,
						var preferenceScore: Double? = null) : JsonModel {

	override fun updateModel(json: JsonObject) {
		with(json) {
			trackId = long("trackId")
			preferenceScore = double("preferenceScore")
		}
	}

	override fun toJSON(json: JsonBuilder) {
		with(json) {
			add("trackId", trackId)
			add("preferenceScore", preferenceScore)
		}
	}
}
