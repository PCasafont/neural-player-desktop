package neuralplayer.desktop

import javafx.application.Application
import javafx.stage.Stage
import neuralplayer.desktop.controller.ConfigController
import neuralplayer.desktop.view.MainView
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/21
 */
class NeuralPlayer : App(MainView::class) {

	private val configController: ConfigController by inject()

	override fun start(stage: Stage) {
		super.start(stage)
		configController.init()
	}
}

fun main(args: Array<String>) {
	Application.launch(NeuralPlayer::class.java, *args)
}
