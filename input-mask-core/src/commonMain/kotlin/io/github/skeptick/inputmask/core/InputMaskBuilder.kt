@file:Suppress("UnusedReceiverParameter")

package io.github.skeptick.inputmask.core

public fun InputMasks.build(builder: InputMaskBuilder.() -> Unit): InputMask {
    return InputMask(DefaultInputMaskBuilder().apply(builder).slots)
}

public interface InputMaskBuilder {

    public val slots: List<InputSlot>

    public fun fixedChar(char: Char, extracted: Boolean)

    public fun singleDigit(required: Boolean)

    public fun singleLetter(required: Boolean)

    public fun singleDigitOrLetter(required: Boolean)

    public fun digits()

    public fun letters()

    public fun digitsOrLetters()

    public operator fun plusAssign(slot: InputSlot)

}

internal open class DefaultInputMaskBuilder : InputMaskBuilder {

    override val slots: MutableList<InputSlot> = mutableListOf()

    private var hasInfiniteSymbols: Boolean = false

    private val lastIsOptionalDigit: Boolean
        get() = slots.lastOrNull()?.isOptionalDigit ?: false

    private val lastIsOptionalLetter: Boolean
        get() = slots.lastOrNull()?.isOptionalLetter ?: false

    private val lastIsOptionalSymbol: Boolean
        get() = slots.lastOrNull()?.isOptional ?: false

    override fun fixedChar(char: Char, extracted: Boolean) {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.SymbolAfterInfiniteSlot("fixed char")
            else -> this += InputSlot.FixedChar(char, extracted)
        }
    }

    override fun singleDigit(required: Boolean) {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.SymbolAfterInfiniteSlot("digit")
            required && lastIsOptionalDigit -> throw InvalidMaskError.RequiredSlotAfterOptionalSlot("digit")
            else -> this += if (required) InputSlot.RequiredDigit else InputSlot.OptionalDigit
        }
    }

    override fun singleLetter(required: Boolean) {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.SymbolAfterInfiniteSlot("letter")
            required && lastIsOptionalLetter -> throw InvalidMaskError.RequiredSlotAfterOptionalSlot("letter")
            else -> this += if (required) InputSlot.RequiredLetter else InputSlot.OptionalLetter
        }
    }

    override fun singleDigitOrLetter(required: Boolean) {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.SymbolAfterInfiniteSlot("digit or letter")
            required && lastIsOptionalSymbol -> throw InvalidMaskError.RequiredSlotAfterOptionalSlot("digit or letter")
            else -> this += if (required) InputSlot.RequiredLetterOrDigit else InputSlot.OptionalLetterOrDigit
        }
    }

    override fun digits() {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.DuplicateInfiniteSlot("digits")
            else -> replaceLast(predicate = { it is InputSlot.OptionalDigit }, newSlot = InputSlot.Digits)
        }
    }

    override fun letters() {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.DuplicateInfiniteSlot("letters")
            else -> replaceLast(predicate = { it is InputSlot.OptionalLetter }, newSlot = InputSlot.Letters)
        }
    }

    override fun digitsOrLetters() {
        when {
            hasInfiniteSymbols -> throw InvalidMaskError.DuplicateInfiniteSlot("digits or letters")
            else -> replaceLast(predicate = { it.isOptional }, newSlot = InputSlot.Letters)
        }
    }

    private fun replaceLast(predicate: (InputSlot) -> Boolean, newSlot: InputSlot) {
        while (slots.lastOrNull()?.let(predicate) == true) slots.removeLast()
        this += newSlot
    }

    override operator fun plusAssign(slot: InputSlot) {
        slots += slot
        if (slot.isInfinite) hasInfiniteSymbols = true
    }

}