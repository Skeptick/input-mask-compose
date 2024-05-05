package io.github.skeptick.inputmask.core.internal

import io.github.skeptick.inputmask.core.InputChange
import io.github.skeptick.inputmask.core.InputChanges
import io.github.skeptick.inputmask.core.InputSlot

internal inline fun buildInputChanges(text: String, builder: InputChangesBuilder.() -> Unit): InputChanges {
    return DefaultInputChangesBuilder(text.length).apply(builder).build()
}

internal interface InputChangesBuilder {
    val textOffset: Int
    fun take(chars: Int)
    fun drop(chars: Int)
    fun preserve(chars: Int)
    fun insert(char: InputSlot.FixedChar)
    fun replace(char: InputSlot.FixedChar)
    fun build(): InputChanges
}

internal class DefaultInputChangesBuilder(private val length: Int) : InputChangesBuilder {

    override var textOffset = 0
    private val actions = ArrayList<InputChange>()
    private var dropCounter = 0
    private var takeCounter = 0
    private var preserveCounter = 0

    override fun take(chars: Int) {
        appendDrop()
        appendPreserve()
        takeCounter += chars
        textOffset += chars
    }

    override fun drop(chars: Int) {
        appendTake()
        appendPreserve()
        dropCounter += chars
        textOffset += chars
    }

    override fun preserve(chars: Int) {
        appendTake()
        appendDrop()
        preserveCounter += chars
        textOffset += chars
    }

    override fun insert(char: InputSlot.FixedChar) {
        appendTake()
        appendDrop()
        appendPreserve()
        actions += InputChange.Insert(char)
    }

    override fun replace(char: InputSlot.FixedChar) {
        appendTake()
        appendPreserve()
        dropCounter += 1
        textOffset += 1
        appendDrop()
        actions += InputChange.Insert(char)
    }

    override fun build(): InputChanges {
        if (textOffset != length) drop(length - textOffset)
        appendTake()
        appendDrop()
        appendPreserve()
        return actions
    }

    private fun appendTake() {
        if (takeCounter != 0) {
            actions += InputChange.Take(takeCounter)
            takeCounter = 0
        }
    }

    private fun appendDrop() {
        if (dropCounter != 0) {
            actions += InputChange.Drop(dropCounter)
            dropCounter = 0
        }
    }

    private fun appendPreserve() {
        if (preserveCounter != 0) {
            actions += InputChange.Preserve(preserveCounter)
            preserveCounter = 0
        }
    }

}