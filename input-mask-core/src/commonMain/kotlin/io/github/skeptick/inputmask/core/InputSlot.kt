package io.github.skeptick.inputmask.core

public sealed interface InputSlot {
    public data class FixedChar(val char: Char, val extracted: Boolean) : InputSlot
    public data object RequiredDigit : InputSlot
    public data object RequiredLetter : InputSlot
    public data object RequiredLetterOrDigit : InputSlot
    public data object OptionalDigit : InputSlot
    public data object OptionalLetter : InputSlot
    public data object OptionalLetterOrDigit : InputSlot
    public data object Digits : InputSlot
    public data object Letters : InputSlot
    public data object LettersOrDigits : InputSlot
}

public val InputSlot.isDigit: Boolean
    get() = when (this) {
        InputSlot.RequiredDigit, InputSlot.OptionalDigit, InputSlot.OptionalLetterOrDigit -> true
        else -> false
    }

public val InputSlot.isOptionalDigit: Boolean
    get() = when (this) {
        InputSlot.OptionalDigit, InputSlot.OptionalLetterOrDigit -> true
        else -> false
    }

public val InputSlot.isOptionalLetter: Boolean
    get() = when (this) {
        InputSlot.OptionalLetter, InputSlot.OptionalLetterOrDigit -> true
        else -> false
    }

public val InputSlot.isOptional: Boolean
    get() = when (this) {
        InputSlot.OptionalDigit, InputSlot.OptionalLetter, InputSlot.OptionalLetterOrDigit -> true
        else -> false
    }

public val InputSlot.isInfinite: Boolean
    get() = when (this) {
        InputSlot.Digits, InputSlot.Letters, InputSlot.LettersOrDigits -> true
        else -> false
    }

public val InputSlot.predicate: (Char) -> Boolean
    get() = when (this) {
        InputSlot.RequiredDigit, InputSlot.OptionalDigit, InputSlot.Digits -> Char::isDigit
        InputSlot.RequiredLetter, InputSlot.OptionalLetter, InputSlot.Letters -> Char::isLetter
        InputSlot.RequiredLetterOrDigit, InputSlot.OptionalLetterOrDigit, InputSlot.LettersOrDigits -> Char::isLetterOrDigit
        is InputSlot.FixedChar -> throw IllegalStateException()
    }