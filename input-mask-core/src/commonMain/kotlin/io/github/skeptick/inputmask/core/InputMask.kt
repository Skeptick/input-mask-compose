package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.internal.parseInputMask

public class InputMask internal constructor(
    public val slots: List<InputSlot>
) {
    public fun isBlank(): Boolean = slots.isEmpty()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val otherInputMask = other as? InputMask ?: return false
        if (slots.size != otherInputMask.slots.size) return false
        for (index in slots.indices) if (slots[index] != otherInputMask.slots[index]) return false
        return true
    }

    override fun hashCode(): Int {
        return slots.hashCode()
    }

    public companion object {
        public val Blank: InputMask = InputMask(emptyList())
    }
}

public object InputMasks {

    private val cache = mutableMapOf<String, InputMask>()

    @Throws(InvalidMaskError::class)
    public fun getOrCreate(mask: String): InputMask = cache.getOrPut(mask) { parseInputMask(mask) }

}