package neuralplayer.desktop.util

import com.jfoenix.controls.*
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.EventTarget
import javafx.geometry.Orientation
import javafx.scene.Node
import tornadofx.*

/**
 * @author Pere
 * @since 2018/04/20
 */

inline fun <T : Node> T.attachTo(parent: EventTarget, op: T.() -> Unit = {}): T = opcr(parent, this, op)

internal inline fun <T : Node> T.attachTo(
		parent: EventTarget,
		after: T.() -> Unit,
		before: (T) -> Unit
) = this.also(before).attachTo(parent, after)

fun EventTarget.button(text: String = "",
					   graphic: Node? = null,
					   op: JFXButton.() -> Unit = {}) = opcr(this, JFXButton(text).apply {
	if (graphic != null) this.graphic = graphic
}, op)

fun <T> EventTarget.combobox(property: Property<T>? = null,
							 values: List<T>? = null,
							 op: JFXComboBox<T>.() -> Unit = {}) = JFXComboBox<T>().attachTo(this, op) {
	if (values != null) it.items = values as? ObservableList<T> ?: values.observable()
	if (property != null) it.bind(property)
}

fun EventTarget.slider(min: Double? = null,
					   max: Double? = null,
					   value: Double? = null,
					   orientation: Orientation? = null,
					   op: JFXSlider.() -> Unit= {}) = opcr(this, JFXSlider().apply {
	if (min != null) this.min = min.toDouble()
	if (max != null) this.max = max.toDouble()
	if (value != null) this.value = value.toDouble()
	if (orientation != null) this.orientation = orientation
}, op)

fun EventTarget.icon(icon: FontAwesomeIcon,
					 size: String? = null,
					 op: FontAwesomeIconView.() -> Unit = {}) = opcr(this, FontAwesomeIconView(icon).apply {
	if (size != null) this.size = size
}, op)

fun <T> EventTarget.listview(values: ObservableList<T>? = null, op: JFXListView<T>.() -> Unit = {}) = JFXListView<T>().attachTo(this, op) {
	if (values != null) {
		if (values is SortedFilteredList<T>) values.bindTo(it)
		else it.items = values
	}
}

fun <T : RecursiveTreeObject<T>> EventTarget.treetableview(root: RecursiveTreeItem<T>? = null, op: JFXTreeTableView<T>.() -> Unit = {}) = JFXTreeTableView<T>().attachTo(this, op) {
	it.root = root
}

//inline fun <reified S : RecursiveTreeObject<S>, T> JFXTreeTableView<S>.column(title: String, prop: KProperty1<S, ObservableValue<T>>): JFXTreeTableColumn<S, T> {
//	val column = JFXTreeTableColumn<S, T>(title)
//	column.cellValueFactory = Callback { prop.call(it.value.value) }
//	addColumnInternal(column)
//	return column
//}
