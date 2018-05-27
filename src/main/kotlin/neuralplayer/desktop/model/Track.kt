package neuralplayer.desktop.model

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*
import kotlin.math.pow

/**
 * @author Pere
 * @since 2018/04/21
 */
class Track(var id: Long?,
			title: String,
			artist: String? = null,
			album: String? = null,
			var filePath: String? = null,
			preferenceScore: Double = 0.0) : RecursiveTreeObject<Track>() {

	val titleProperty = SimpleStringProperty(title)
	var title: String by titleProperty

	val artistProperty = SimpleStringProperty(artist)
	var artist: String? by artistProperty

	val albumProperty = SimpleStringProperty(album)
	var album: String? by albumProperty

	val preferenceScoreProperty = SimpleDoubleProperty(preferenceScore)
	var preferenceScore: Double by preferenceScoreProperty

	var timesPlayed = 0
	val preferenceWeight: Double
		get() {
		val exponent = preferenceScore.let { if (it > 0) it else 10.0 }
			return 5.0.pow(exponent * 0.9.pow(timesPlayed))
	}
}
