package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.FormatResult.Companion.Incomplete

public data class FormatResult(
    val value: String,
    val isComplete: Boolean
) {
    public companion object {

        public operator fun invoke(mask: InputMask): FormatResult =
            FormatResult(
                value = "",
                isComplete = mask.isBlank()
            )

        @Suppress("FunctionName")
        internal fun Incomplete(value: CharSequence): FormatResult =
            FormatResult(
                value = value.toString(),
                isComplete = false
            )
    }
}

public fun InputMask.format(text: String): FormatResult {
    return format(text = text, isExtraction = false)
}

public fun InputMask.extract(text: String): FormatResult {
    return format(text = text, isExtraction = true)
}

public fun InputMask.format(text: String, isExtraction: Boolean): FormatResult {
    if (isBlank()) return FormatResult(text, isComplete = true)
    val source = StringBuilder(text)
    return FormatResult(
        isComplete = true,
        value = buildString {
            for (slot in slots) when (slot) {
                is InputSlot.FixedChar -> {
                    if (!isExtraction || slot.extracted) append(slot.char)
                }
                InputSlot.RequiredDigit, InputSlot.RequiredLetter, InputSlot.RequiredLetterOrDigit -> {
                    swapFirst(source = source, target = this, predicate = slot.predicate) ?: return Incomplete(this)
                }
                InputSlot.OptionalDigit, InputSlot.OptionalLetter, InputSlot.OptionalLetterOrDigit -> {
                    swapFirst(source = source, target = this, predicate = slot.predicate)
                }
                InputSlot.Digits, InputSlot.Letters, InputSlot.LettersOrDigits -> {
                    swapWhile(source = source, target = this, predicate = slot.predicate)
                }
            }
        }
    )
}

public fun InputMask.clear(text: String): String {
    if (isBlank()) return text
    val source = StringBuilder(text)
    return buildString {
        for (slot in slots) when (slot) {
            is InputSlot.FixedChar -> continue
            InputSlot.RequiredDigit, InputSlot.RequiredLetter, InputSlot.RequiredLetterOrDigit -> {
                swapFirst(source = source, target = this, predicate = slot.predicate) ?: return this.toString()
            }
            InputSlot.OptionalDigit, InputSlot.OptionalLetter, InputSlot.OptionalLetterOrDigit -> {
                swapFirst(source = source, target = this, predicate = slot.predicate)
            }
            InputSlot.Digits, InputSlot.Letters, InputSlot.LettersOrDigits -> {
                swapWhile(source = source, target = this, predicate = slot.predicate)
            }
        }
    }
}

private inline fun swapFirst(source: StringBuilder, target: StringBuilder, predicate: (Char) -> Boolean): Char? {
    return source.popFirstOrNull(predicate)?.also(target::append)
}

private inline fun swapWhile(source: StringBuilder, target: StringBuilder, predicate: (Char) -> Boolean) {
    source.popWhile(predicate).let(target::append)
}

private inline fun StringBuilder.popFirstOrNull(predicate: (Char) -> Boolean): Char? {
    return if (firstOrNull()?.let(predicate) == true) get(0).also { deleteAt(0) } else null
}

private inline fun StringBuilder.popWhile(predicate: (Char) -> Boolean): CharSequence {
    return takeWhile(predicate).also { deleteRange(0, it.length) }
}