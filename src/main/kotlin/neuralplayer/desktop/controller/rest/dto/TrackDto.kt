package neuralplayer.desktop.controller.rest.dto

import neuralplayer.desktop.model.Track
import tornadofx.*
import javax.json.JsonObject

/**
 * @author Pere
 * @since 2018/04/21
 */
class TrackDto(var id: Long? = null,
			   var title: String? = null,
			   var artist: String? = null,
			   var album: String? = null,
			   var fileExtension: String? = null,
			   var preferenceScore: Double? = null) : JsonModel {

	constructor(other: Track) : this(other.id, other.title, other.artist, other.album)

	override fun updateModel(json: JsonObject) {
		with(json) {
			id = long("id")
			title = string("title")
			artist = string("artist")
			album = string("album")
			fileExtension = string("fileExtension")
			preferenceScore = double("preferenceScore")
		}
	}

	override fun toJSON(json: JsonBuilder) {
		with(json) {
			add("id", id)
			add("title", title)
			add("artist", artist)
			add("album", album)
			add("fileExtension", fileExtension)
			add("preferenceScore", preferenceScore)
		}
	}
}
