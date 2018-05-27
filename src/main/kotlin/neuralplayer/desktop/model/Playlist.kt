package neuralplayer.desktop.model

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import javafx.beans.property.SimpleListProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/21
 */
class Playlist(var id: Long,
			   name: String,
			   creator: String? = null,
			   tracks: List<Track>? = emptyList()) : RecursiveTreeObject<Playlist>() {

	val nameProperty = SimpleStringProperty(name)
	var name: String by nameProperty

	val creatorProperty = SimpleStringProperty(creator)
	var creator: String? by creatorProperty

	val tracksProperty = SimpleListProperty<Track>(FXCollections.observableArrayList(tracks))
	var tracks by tracksProperty

	override fun toString() = name
}
