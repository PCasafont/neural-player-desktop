package neuralplayer.desktop.controller.rest.dto

import neuralplayer.desktop.model.Playlist
import tornadofx.*
import javax.json.JsonObject

/**
 * @author Pere
 * @since 2018/04/21
 */
class PlaylistDto(var id: Long? = null,
				  var name: String? = null,
				  var creator: String? = null,
				  var tracks: MutableList<Long>? = null) : JsonModel {

	constructor(other: Playlist) : this(other.id, other.name, other.creator, other.tracks.map { it.id!! }.toMutableList())

	override fun updateModel(json: JsonObject) {
		with(json) {
			id = long("id")
			name = string("name")
			creator = string("creator")
			tracks = jsonArray("tracks")?.toList()?.map { it.toString().toLong() }?.toMutableList()
		}
	}

	override fun toJSON(json: JsonBuilder) {
		with(json) {
			add("id", id)
			add("name", name)
			add("creator", creator)
			add("tracks", tracks)
		}
	}
}
