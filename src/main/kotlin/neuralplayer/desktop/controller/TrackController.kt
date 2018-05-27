package neuralplayer.desktop.controller

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.media.Media
import neuralplayer.desktop.controller.rest.TrackRestController
import neuralplayer.desktop.controller.rest.dto.TrackDto
import neuralplayer.desktop.model.Track
import neuralplayer.desktop.util.MediaListener
import neuralplayer.desktop.view.NewTrackView
import tornadofx.*
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

/**
 * @author Pere
 * @since 2018/04/22
 */
class TrackController : Controller() {

	private val trackRestController: TrackRestController by inject()

	val tracks: ObservableList<Track> = FXCollections.observableArrayList()

	fun init() {
		Files.createDirectories(Paths.get("./tracks/"))
		tracks.addAll(trackRestController.getAll().map { Track(it.id!!, it.title!!, it.artist, it.album,
				if (it.fileExtension != null) "./tracks/${it.id}.${it.fileExtension!!}" else null, it.preferenceScore ?: 0.0) })
		tracks.sortBy { (it.artist + it.album).toLowerCase() }
	}

	fun add(files: List<File>) {
		for (file in files) {
			if (file.isDirectory) {
				add(file.listFiles().toList())
				continue
			}
			if (file.extension !in arrayOf("mp3", "wav")) {
				continue
			}
			try {
				val media = Media(file.toURI().toURL().toString())
				media.metadata.addListener(MediaListener(file.nameWithoutExtension, find(NewTrackView::class)) { title, artist, album ->
					val trackDto = TrackDto(null, title, artist, album)
					val id = trackRestController.create(trackDto, file)
					val track = Track(id, title!!, artist, album, "./tracks/$id.${file.extension}")
					runLater {
						val existingTrack = tracks.find { it.id == id }
						if (existingTrack != null) {
							existingTrack.filePath = track.filePath
						} else {
							tracks.add(track)
							tracks.sortBy { (it.artist + it.album).toLowerCase() }
						}
					}
				})
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	fun downloadIfNecessary(track: Track) {
		if (track.filePath == null) {
			return
		}

		val path = Paths.get(track.filePath)
		if (Files.exists(path)) {
			return
		}

		val trackData = trackRestController.download(track.id!!)
		Files.write(path, trackData.readBytes())
	}

	fun updateTrackScore(track: Track, score: Double) {
		track.preferenceScoreProperty.set(score)
		val trackDto = TrackDto(track)
		trackDto.preferenceScore = score
		trackRestController.update(track.id!!, trackDto)
	}
}
