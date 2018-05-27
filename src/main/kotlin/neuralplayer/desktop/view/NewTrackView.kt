package neuralplayer.desktop.view

import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class NewTrackView : View("New Track") {

	var open = true
	val fileNameProperty = SimpleStringProperty()
	lateinit var onSubmit: (String?, String?, String?) -> Unit

	override val root = form {
		val title = SimpleStringProperty()
		val artist = SimpleStringProperty()
		val album = SimpleStringProperty()
		fieldset("New Track") {
			label(fileNameProperty)
			field("Title") {
				textfield(title)
			}
			field("Artist") {
				textfield(artist)
			}
			field("Album") {
				textfield(album)
			}
			button("Submit") {
				action {
					onSubmit(title.value, artist.value, album.value)
					open = false
					close()
				}
			}
		}
	}

	override fun onUndock() {
		if (open) {
			onSubmit(fileNameProperty.value, null, null)
		}
	}

	override fun onDelete() {
		if (open) {
			onSubmit(fileNameProperty.value, null, null)
		}
	}
}
