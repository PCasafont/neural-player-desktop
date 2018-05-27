package neuralplayer.desktop.controller.rest

import neuralplayer.desktop.controller.rest.dto.PlaylistDto
import tornadofx.*



/**
 * @author Pere
 * @since 2018/04/22
 */
class PlaylistRestController : Controller() {

	private val api: Rest by inject()

	fun getAll()
			= api.get("playlists").list().toModel<PlaylistDto>()
	fun create(playlistDto: PlaylistDto)
			= api.post("playlists", playlistDto).one().toModel<PlaylistDto>()
	fun update(id: Long, playlistDto: PlaylistDto)
			= api.put("playlists/$id", playlistDto).one().toModel<PlaylistDto>()
	fun delete(id: Long)
			= api.delete("playlists/$id")
}
