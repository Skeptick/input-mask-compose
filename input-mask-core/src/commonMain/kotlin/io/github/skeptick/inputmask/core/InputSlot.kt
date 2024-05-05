package io.github.skeptick.inputmask.core

public sealed interface SlotType {
    public sealed interface Fixed : SlotType
    public sealed interface Required : SlotType
    public sealed interface Optional : SlotType
    public sealed interface Infinite : SlotType
}

public sealed interface SlotSymbols {
    public sealed interface Fixed : SlotSymbols
    public sealed interface Digit : SlotSymbols
    public sealed interface Letter : SlotSymbols
    public sealed interface DigitOrLetter : SlotSymbols
}

public sealed interface InputSlot : SlotType, SlotSymbols {
    public data class FixedChar(val char: Char, val extracted: Boolean) : InputSlot, SlotType.Fixed, SlotSymbols.Fixed
    public data object RequiredDigit : InputSlot, SlotType.Required, SlotSymbols.Digit
    public data object RequiredLetter : InputSlot, SlotType.Required, SlotSymbols.Letter
    public data object RequiredLetterOrDigit : InputSlot, SlotType.Required, SlotSymbols.DigitOrLetter
    public data object OptionalDigit : InputSlot, SlotType.Optional, SlotSymbols.Digit
    public data object OptionalLetter : InputSlot, SlotType.Optional, SlotSymbols.Letter
    public data object OptionalLetterOrDigit : InputSlot, SlotType.Optional, SlotSymbols.DigitOrLetter
    public data object Digits : InputSlot, SlotType.Infinite, SlotSymbols.Digit
    public data object Letters : InputSlot, SlotType.Infinite, SlotSymbols.Letter
    public data object LettersOrDigits : InputSlot, SlotType.Infinite, SlotSymbols.DigitOrLetter
}

public val InputSlot.isDigit: Boolean
    get() = this is SlotSymbols.Digit

public val InputSlot.isOptionalDigit: Boolean
    get() = this is InputSlot.OptionalDigit || this is InputSlot.OptionalLetterOrDigit

public val InputSlot.isOptionalLetter: Boolean
    get() = this is InputSlot.OptionalLetter || this is InputSlot.OptionalLetterOrDigit

public val InputSlot.isOptional: Boolean
    get() = this is SlotType.Optional

public val InputSlot.isInfinite: Boolean
    get() = this is SlotType.Infinite

public val InputSlot.predicate: (Char) -> Boolean
    get() = when (this) {
        is InputSlot.FixedChar -> { it -> it == char }
        is SlotSymbols.Digit -> Char::isDigit
        is SlotSymbols.Letter -> Char::isLetter
        is SlotSymbols.DigitOrLetter -> Char::isLetterOrDigit
    }