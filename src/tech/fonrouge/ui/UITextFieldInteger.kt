package tech.fonrouge.ui

import javafx.scene.control.TextField
import tech.fonrouge.MOODB.MField

@Suppress("unused")
class UITextFieldInteger : TextField(), UIBinding<Int> {
    override var mField: MField<Int>? = null
        get() = field
        set(value) {
            field = value
        }
}
