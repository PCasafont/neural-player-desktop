package neuralplayer.desktop.controller

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import neuralplayer.desktop.controller.rest.PlaylistRestController
import neuralplayer.desktop.controller.rest.dto.PlaylistDto
import neuralplayer.desktop.model.Playlist
import neuralplayer.desktop.model.Track
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/22
 */
class PlaylistController : Controller() {

	private val playlistRestController: PlaylistRestController by inject()
	private val trackController: TrackController by inject()

	val playlists: ObservableList<Playlist> = FXCollections.observableArrayList()

	val currentPlaylistProperty = SimpleObjectProperty<Playlist>(null)
	var currentPlaylist: Playlist? by currentPlaylistProperty

	private val presentProperties = HashMap<Track, BooleanProperty>()

	fun init() {
		val allPlaylist = Playlist(-1, "All", null, trackController.tracks)
		allPlaylist.tracksProperty.bind(trackController.tracks, { it })
		playlists.add(allPlaylist)
		playlists.addAll(playlistRestController.getAll().map {
			Playlist(it.id!!, it.name!!, it.creator, it.tracks!!.map {id ->
				trackController.tracks.find { it.id == id }!!
			})
		})
		currentPlaylist = allPlaylist
	}

	fun create(name: String?): Playlist {
		val dto = playlistRestController.create(PlaylistDto(name = name))
		val playlist = Playlist(dto.id!!, dto.name!!, dto.creator, emptyList())
		playlists += playlist
		currentPlaylist = playlist
		return playlist
	}

	fun updateCurrent(dto: PlaylistDto) {
		val id = dto.id!!
		val updated = playlistRestController.update(id, dto)
		playlists.find { it.id == id }?.let {
			it.name = updated.name!!
			it.tracksProperty.setAll(updated.tracks!!.map { id ->
				trackController.tracks.find { it.id == id }!!
			})
		} ?: playlists.add(Playlist(updated.id!!, updated.name!!, updated.creator, updated.tracks!!.map {trackId ->
				trackController.tracks.find { it.id == trackId }!!
		}))
	}

	fun deleteCurrent() {
		currentPlaylist?.let {
			if (it.id == -1L) {
				return
			}
			playlistRestController.delete(it.id)
			playlists.remove(it)
			currentPlaylist = null
		}
	}

	private fun isPresent(track: Track, playlist: PlaylistDto): Boolean {
		return playlist.tracks?.any { it == track.id } ?: false
	}

	fun presentProperty(track: Track, playlist: PlaylistDto): BooleanProperty = presentProperties.getOrPut(track) {
		val property = SimpleBooleanProperty(isPresent(track, playlist))
		property.addListener { _, oldValue, newValue ->
			if (oldValue != newValue) {
				if (newValue) {
					playlist.tracks!!.add(track.id!!)
				} else {
					playlist.tracks!!.remove(track.id!!)
				}
			}
		}
		//currentPlaylistProperty.addListener { _, _, newValue ->
		//	property.set(isPresent(track, newValue))
		//}

		return property
	}

	fun getRandomTrack(): Track? {
		val playlist = currentPlaylist ?: return null

		val totalTracksWeight = playlist.tracks.sumByDouble { it.preferenceWeight }
		val random = Math.random() * totalTracksWeight
		var cumulative = 0.0
		for (track in playlist.tracks) {
			cumulative += track.preferenceWeight
			if (cumulative > random) {
				return track
			}
		}

		return null
	}
}
