package neuralplayer.desktop.util

import javafx.collections.MapChangeListener
import neuralplayer.desktop.view.NewTrackView
import tornadofx.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * @author Pere
 */
class MediaListener(private val fileName: String,
					private val completionView: NewTrackView,
					private val handler: (String?, String?, String?) -> Unit) : MapChangeListener<String, Any> {

	var title: String? = null
	var artist: String? = null
	var album: String? = null

	init {
		if (timerTask?.isDone == false) {
			executionQueue.add(this)
		} else {
			timerTask = executorService.schedule({
				openCompletionView()
			}, 1500, TimeUnit.MILLISECONDS)
		}
	}

	override fun onChanged(ch: MapChangeListener.Change<out String, *>) {
		if (ch.wasAdded()) {
			val key = ch.key
			when (key) {
				"title" -> title = ch.valueAdded as String
				"artist" -> artist = ch.valueAdded as String
				"album" -> album = ch.valueAdded as String
			}
		}

		if (timerTask?.isDone == false) {
			executionQueue.add(this)
		} else {
			timerTask = executorService.schedule({
				if (title == null) {
					openCompletionView()
				} else {
					handle(title, artist, album)
				}
			}, 500, TimeUnit.MILLISECONDS)
		}
	}

	private fun handle(title: String?, artist: String?, album: String?) {
		handler(title, artist, album)
		val next = executionQueue.firstOrNull() ?: return
		executionQueue.remove(next)
		if (next.title != null) {
			next.handle(title, artist, album)
		} else {
			next.openCompletionView()
		}
	}

	private fun openCompletionView() {
		completionView.open = true
		completionView.fileNameProperty.set(fileName)
		completionView.onSubmit = { title, artist, album ->
			handle(title, artist, album)
		}
		runLater {
			completionView.openModal()
		}
	}

	override fun equals(other: Any?) = other is MediaListener && other.fileName == fileName

	companion object {
		private val executionQueue = HashSet<MediaListener>()
		private val executorService = Executors.newScheduledThreadPool(1)
		private var timerTask: ScheduledFuture<*>? = null
	}
}
