package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.internal.buildInputMask
import kotlin.jvm.JvmInline

@JvmInline
public value class InputMask internal constructor(public val slots: List<InputSlot>) {
    public fun isBlank(): Boolean = slots.isEmpty()
}

public object InputMasks {

    private val cache = mutableMapOf<String, InputMask>()

    public fun getOrCreate(mask: String): InputMask = cache.getOrPut(mask) { buildInputMask(mask) }

}