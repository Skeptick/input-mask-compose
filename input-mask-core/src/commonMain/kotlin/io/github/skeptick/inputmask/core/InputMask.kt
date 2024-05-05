package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.internal.parseInputMask

public class InputMask internal constructor(
    public val slots: List<InputSlot>
) {
    public fun isBlank(): Boolean = slots.isEmpty()

    public companion object {
        public val Blank: InputMask = InputMask(emptyList())
    }
}

public object InputMasks {

    private val cache = mutableMapOf<String, InputMask>()

    @Throws(InvalidMaskError::class)
    public fun getOrCreate(mask: String): InputMask = cache.getOrPut(mask) { parseInputMask(mask) }

}