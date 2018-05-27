package neuralplayer.desktop.view

import com.jfoenix.controls.RecursiveTreeItem
import javafx.collections.FXCollections
import javafx.scene.control.TreeTableColumn
import javafx.scene.input.TransferMode
import javafx.util.Callback
import neuralplayer.desktop.controller.PlayerController
import neuralplayer.desktop.controller.PlaylistController
import neuralplayer.desktop.controller.TrackController
import neuralplayer.desktop.controller.UserController
import neuralplayer.desktop.controller.rest.dto.PlaylistDto
import neuralplayer.desktop.model.Track
import neuralplayer.desktop.util.treetableview
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/21
 */
class MainView : View("Neural Player") {

	private val playerView: PlayerView by inject()
	private val playerController: PlayerController by inject()
	private val userController: UserController by inject()
	private val trackController: TrackController by inject()
	private val playlistController: PlaylistController by inject()

	override val root = borderpane {
		left = vbox {
			hbox {
				label("Playlists")
				button("New...") {
					setOnAction {
						dialog("New playlist") {
							label("Name:")
							val textField = textfield("New Playlist")
							button("Create") {
								setOnAction {
									this@dialog.close()
									playlistController.create(textField.text)
								}
							}
						}
					}
				}
				button("Edit") {
					setOnAction {
						val playlist = playlistController.currentPlaylist ?: return@setOnAction
						if (playlist.id == -1L) {
							return@setOnAction
						}
						dialog("Edit playlist") {
							val editedPlaylist = PlaylistDto(playlist)
							prefWidth = 700.0
							prefHeight = 500.0
							label("Name:")
							val textField = textfield(editedPlaylist.name)
							treetableview<Track>(RecursiveTreeItem(trackController.tracks, { it.children })) {
								val presentColumn = TreeTableColumn<Track, Boolean>()
								presentColumn.cellValueFactory = Callback { playlistController.presentProperty(it.value.value, editedPlaylist) }
								presentColumn.cellFormat {
									//graphic = checkbox(property = this.itemProperty())
									graphic = checkbox(property = playlistController.presentProperty(this.rowItem, editedPlaylist))
								}
								columns.add(presentColumn)
								column("Title", Track::titleProperty)
								column("Artist", Track::artistProperty)
								column("Album", Track::albumProperty)

								root.isExpanded = true
								isShowRoot = false
							}
							button("Submit") {
								setOnAction {
									this@dialog.close()
									editedPlaylist.name = textField.text
									playlistController.updateCurrent(editedPlaylist)
								}
							}
						}
					}
				}
				button("Delete") {
					setOnAction {
						playlistController.deleteCurrent()
					}
				}
			}
			listview(playlistController.playlists) {
				cellFormat {
					graphic = label(it.nameProperty)
				}
				bindSelected(playlistController.currentPlaylistProperty)
				playlistController.currentPlaylistProperty.addListener { _, oldValue, newValue ->
					if (oldValue != newValue) {
						selectionModel.select(newValue)
					}
				}
				selectionModel.select(0)
			}
		}
		val trackList = FXCollections.observableArrayList<Track>()
		playlistController.currentPlaylist?.tracks?.let {
			trackList.setAll(it)
		}
		playlistController.currentPlaylistProperty.addListener { _, _, newValue ->
			newValue?.apply {
				trackList.clear()
				trackList.bind(tracksProperty, { it })
			}
		}
		center = treetableview<Track>(RecursiveTreeItem(trackList, { it.children })) {
			val title = column("Title", Track::titleProperty)
			title.prefWidth = 300.0
			val artist = column("Artist", Track::artistProperty)
			artist.prefWidth = 150.0
			val album = column("Album", Track::albumProperty)
			album.prefWidth = 150.0
			//val rating = column("Rating", Track::preferenceScoreProperty)
			//rating.prefWidth = 50.0

			root.isExpanded = true
			isShowRoot = false

			bindSelected(playerController.currentTrackProperty)
		}

		bottom = playerView.root

		setOnDragOver {
			it.acceptTransferModes(TransferMode.COPY)
		}
		setOnDragDropped {
			trackController.add(it.dragboard.files)
		}
	}
}
