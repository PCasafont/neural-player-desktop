package neuralplayer.desktop.view

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.Priority
import javafx.scene.text.FontWeight
import javafx.scene.text.TextAlignment
import javafx.util.Callback
import neuralplayer.desktop.controller.PlayerController
import neuralplayer.desktop.controller.TrackController
import neuralplayer.desktop.util.button
import neuralplayer.desktop.util.icon
import neuralplayer.desktop.util.slider
import org.controlsfx.control.Rating
import tornadofx.*
import java.util.concurrent.atomic.AtomicBoolean

class PlayerView : View() {

	private val playerController: PlayerController by inject()
	private val trackController: TrackController by inject()

	override val root = hbox(30.0, Pos.CENTER) {
		prefWidth = 1000.0
		prefHeight = 100.0
		padding = Insets(5.0)
		val initialTrack = playerController.currentTrack
		vbox(5.0, Pos.CENTER_LEFT) {
			paddingLeft = 10.0
			//minWidth = 150.0
			minWidth = 300.0
			maxWidth = 300.0
			hgrow = Priority.ALWAYS
			label {
				text = initialTrack?.title ?: ""
				playerController.currentTrackProperty.addListener { _, _, currentTrack ->
					text = currentTrack?.title ?: ""
				}
				textAlignment = TextAlignment.CENTER
				style {
					fontSize = 16.px
				}
			}
			label {
				text = if (initialTrack?.artist != null && initialTrack.album != null) {
					initialTrack.artist + " - " + initialTrack.album
				} else {
					initialTrack?.artist ?: initialTrack?.album ?: ""
				}
				playerController.currentTrackProperty.addListener { _, _, currentTrack ->
					text = if (currentTrack?.artist != null && currentTrack.album != null) {
						currentTrack.artist + " - " + currentTrack.album
					} else {
						currentTrack?.artist ?: currentTrack?.album ?: ""
					}
				}
				textAlignment = TextAlignment.CENTER
				style {
					fontSize = 14.px
					fontWeight = FontWeight.BOLD
				}
			}
		}
		vbox(10.0, Pos.CENTER) {
			minWidth = 250.0
			hgrow = Priority.ALWAYS
			hbox(20.0, Pos.CENTER) {
				button("", icon(FontAwesomeIcon.STEP_BACKWARD, "2em")) {
					action {
						playerController.selectPrevious()
					}
				}
				val iconView = icon(FontAwesomeIcon.PLAY, "2em")
				button("", iconView) {
					playerController.playingProperty.addListener { _, _, playing ->
						if (playing) {
							iconView.setIcon(FontAwesomeIcon.PAUSE)
						} else {
							iconView.setIcon(FontAwesomeIcon.PLAY)
						}
					}
					action {
						if (playerController.playing) {
							playerController.pause()
						} else {
							playerController.play()
						}
					}
				}
				button("", icon(FontAwesomeIcon.STEP_FORWARD, "2em")) {
					action {
						playerController.selectNext()
					}
				}
			}
			hbox {
				spacing = 20.0
				alignment = Pos.CENTER
				hgrow = Priority.ALWAYS
				label("") {
					playerController.currentTimeProperty.addListener { _, _, currentTime ->
						currentTime?.let { text = durationToString(it.toSeconds()) }
					}
				}
				slider(0.0, 1.0, 0.0) {
					alignment = Pos.CENTER
					hgrow = Priority.ALWAYS
					valueFactory = Callback {
						stringBinding(it, it.valueProperty()) {
							durationToString(it.value)
						}
					}
					val wasPlaying = AtomicBoolean(false)
					setOnMousePressed {
						wasPlaying.set(playerController.playing)
						playerController.pause()
					}
					setOnMouseReleased {
						playerController.seek(value.toInt())
						if (wasPlaying.get()) {
							playerController.play()
						}
					}
					playerController.currentTimeProperty.addListener { _, _, currentTime ->
						//println(currentTime)
						currentTime?.let { value = it.toSeconds() }
					}
					playerController.totalDurationProperty.addListener { _, _, totalDuration ->
						totalDuration?.let { max = it.toSeconds() }
					}
				}
				label("") {
					playerController.totalDurationProperty.addListener { _, _, totalDuration ->
						totalDuration?.let { text = durationToString(it.toSeconds()) }
					}
				}
			}
		}
		vbox(10.0, Pos.CENTER) {
			hgrow = Priority.ALWAYS
			val rating = Rating()
			rating.isPartialRating = true
			if (initialTrack != null) {
				rating.isVisible = true
				rating.rating = initialTrack.preferenceScore
			} else {
				rating.isVisible = false
			}
			val changingTrack = AtomicBoolean(false)
			rating.ratingProperty().addListener { _, _, newValue ->
				if (!changingTrack.get()) {
					playerController.currentTrack?.let {
						trackController.updateTrackScore(it, newValue.toDouble())
					}
				}
			}
			playerController.currentTrackProperty.addListener { _, _, currentTrack ->
				if (currentTrack != null) {
					changingTrack.set(true)
					rating.isVisible = true
					rating.rating = currentTrack.preferenceScore
					changingTrack.set(false)
				} else {
					rating.isVisible = false
				}
			}
			add(rating)
		}
	}

	private fun durationToString(durationSeconds: Double): String {
		if (durationSeconds.isNaN()) {
			return ""
		}
		val minutes = durationSeconds.toInt() / 60
		return String.format("$minutes:%02d", durationSeconds.toInt() % 60)
	}
}
