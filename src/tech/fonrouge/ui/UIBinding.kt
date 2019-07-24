package tech.fonrouge.ui

import tech.fonrouge.MOODB.MField

interface UIBinding<T> {
    var mField: MField<T>?
}