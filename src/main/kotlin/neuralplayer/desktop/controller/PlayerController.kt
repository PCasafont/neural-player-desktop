package neuralplayer.desktop.controller

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import neuralplayer.desktop.model.Track
import tornadofx.*
import java.io.File
import java.util.*

/**
 * @author Pere
 * @since 2018/04/22
 */
class PlayerController : Controller() {

	private val trackController: TrackController by inject()
	private val playlistController: PlaylistController by inject()

	val currentTrackProperty = SimpleObjectProperty<Track?>()
	var currentTrack: Track? by currentTrackProperty

	val totalDurationProperty = SimpleObjectProperty(Duration.ZERO)
	val currentTimeProperty = SimpleObjectProperty(Duration.ZERO)

	val playingProperty = SimpleBooleanProperty(false)
	var playing: Boolean by playingProperty

	var mediaPlayer: MediaPlayer? = null

	private val previousTracks = Stack<Track>()
	private val nextTracks = Stack<Track>()

	fun init() {
		currentTrackProperty.addListener { _, _, newTrack ->
			newTrack?.let { it.timesPlayed++ }
			selectTrack(newTrack)
		}
		selectNext()
	}

	private fun selectTrack(track: Track?) {
		mediaPlayer?.apply {
			dispose()
		}

		if (track?.filePath != null) {
			trackController.downloadIfNecessary(track)
			mediaPlayer = MediaPlayer(createMedia(track.filePath!!))
			mediaPlayer?.apply {
				onEndOfMedia = Runnable { selectNext() }
				totalDurationProperty.bind(totalDurationProperty())
				//println(currentTimeProperty().get().toString() + " " + currentTime)
				currentTimeProperty.bind(currentTimeProperty())
				//println(currentTimeProperty().get().toString() + " " + currentTime)
				if (playing) {
					play()
				}
			} ?: playingProperty.set(false)
		} else {
			mediaPlayer = null
			playing = false
			totalDurationProperty.unbind()
			totalDurationProperty.set(Duration(0.0))
			currentTimeProperty.unbind()
			currentTimeProperty.set(Duration(0.0))
		}
	}

	fun play() = mediaPlayer?.apply {
		currentTimeProperty.bind(currentTimeProperty())
		play()
		playing = true
	}

	fun pause() {
		currentTimeProperty.unbind()
		mediaPlayer?.pause()
		playing = false
	}

	fun seek(second: Int) {
		if (!currentTimeProperty.isBound) {
			currentTimeProperty.set(Duration(second * 1000.0))
		}
		mediaPlayer?.apply {
			seek(Duration(second * 1000.0))
		}
	}

	fun selectNext() {
		if (previousTracks.isEmpty() || previousTracks.peek() !== currentTrack) {
			previousTracks.push(currentTrack)
		}

		if (!nextTracks.isEmpty()) {
			currentTrack = nextTracks.pop()
		} else {
			currentTrack = playlistController.getRandomTrack()
		}
	}

	fun selectPrevious() {
		if (!previousTracks.isEmpty()) {
			nextTracks.push(currentTrack)
			currentTrack = previousTracks.pop()
		}
	}

	/**
	 * Creates a media player corresponding to the desired file.
	 *
	 * @param path The path of the file we wish to play.
	 * @return The newly created media player for that file.
	 */
	private fun createMedia(path: String) = Media(File(path).toURI().toURL().toString())
}
