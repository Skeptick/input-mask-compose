package io.github.skeptick.inputmask.core.internal

import io.github.skeptick.inputmask.core.FormatResult
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputSlot
import io.github.skeptick.inputmask.core.SlotType
import io.github.skeptick.inputmask.core.predicate

internal fun InputMask.process(text: String, replacePrefix: Boolean): FormatResult {
    if (isBlank()) return FormatResult.Empty(text)

    return FormatResult(
        sourceValue = text,
        isComplete = true,
        inputChanges = buildInputChanges(text) {
            var lastOptionalFilled = true
            slots.fastForEach { slot ->
                when (slot) {
                    is SlotType.Required -> {
                        val charTaken = takeFirst(text, slot.predicate)
                        if (!charTaken) return FormatResult.Incomplete(text, build())
                    }
                    is SlotType.Optional -> {
                        lastOptionalFilled = text.isCharValidAt(textOffset, slot.predicate)
                        if (lastOptionalFilled) take(chars = 1)
                    }
                    is InputSlot.FixedChar -> when {
                        textOffset == 0 && !replacePrefix -> {
                            insert(slot)
                        }
                        lastOptionalFilled -> {
                            val canReplace = text.isCharValidAt(textOffset, slot.predicate)
                            if (canReplace) replace(slot) else insert(slot)
                        }
                        else -> {
                            val slotFilled = replaceFirst(text, slot)
                            if (!slotFilled) return FormatResult.Incomplete(text, build())
                            lastOptionalFilled = true
                        }
                    }
                    is SlotType.Infinite -> {
                        takeAll(text, slot.predicate)
                    }
                }
            }
        }
    )
}

private inline fun InputChangesBuilder.takeAll(text: String, predicate: (Char) -> Boolean) {
    while (textOffset != text.length) {
        val substring = text.substring(textOffset)
        val validChars = substring.countWhile(predicate)
        if (validChars != 0) take(validChars) else drop(substring.countWhileNot(predicate))
    }
}

private inline fun InputChangesBuilder.takeFirst(text: String, predicate: (Char) -> Boolean): Boolean {
    return moveOffsetToFirst(text, predicate).also {
        if (it) take(chars = 1)
    }
}

private inline fun InputChangesBuilder.replaceFirst(text: String, fixedChar: InputSlot.FixedChar): Boolean {
    return moveOffsetToFirst(text, fixedChar.predicate).also { found ->
        if (!found) return@also
        if (fixedChar.extracted) replace(fixedChar) else preserve(chars = 1)
    }
}

private inline fun InputChangesBuilder.moveOffsetToFirst(text: String, predicate: (Char) -> Boolean): Boolean {
    return when {
        textOffset == text.length -> false
        text.isCharValidAt(textOffset, predicate) -> true
        else -> {
            val substring = text.substring(textOffset)
            drop(substring.countWhileNot(predicate))
            textOffset != text.length
        }
    }
}