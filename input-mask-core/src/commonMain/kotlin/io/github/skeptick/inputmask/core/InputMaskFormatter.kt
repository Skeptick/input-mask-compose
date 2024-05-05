@file:Suppress("FunctionName")

package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.internal.fastForEach
import io.github.skeptick.inputmask.core.internal.fastLazy
import io.github.skeptick.inputmask.core.internal.process

public data class FormatResult internal constructor(
    val sourceValue: String,
    val inputChanges: InputChanges,
    val isComplete: Boolean
) {

    val formattedValue: String by fastLazy { formattedInput(inputChanges, sourceValue) }
    val extractedValue: String by fastLazy { extractedInput(inputChanges, sourceValue) }
    val clearedValue: String by fastLazy { clearedInput(inputChanges, sourceValue) }

    public companion object {

        public fun Empty(sourceValue: String): FormatResult =
            FormatResult(
                sourceValue = sourceValue,
                isComplete = true,
                inputChanges = listOf(
                    InputChange.Take(sourceValue.length)
                )
            )

        internal fun Incomplete(text: String, inputChanges: InputChanges) =
            FormatResult(
                sourceValue = text,
                inputChanges = inputChanges,
                isComplete = false
            )

    }
}

/**
 * @param[replacePrefix] If false, then characters that are the mask prefix will be unconditionally added to the result.
 *
 * Example:
 * ```mask = 123-[000]``` (prefix is `123-`)
 * ```mask.format(text = 123456, replacePrefix = true)``` = 123-456
 * ```mask.format(text = 123456, replacePrefix = false)``` = 123-123 (characters outside the mask were omitted)
 */
public fun InputMask.format(text: String, replacePrefix: Boolean = true): FormatResult {
    return process(text, replacePrefix)
}

private fun formattedInput(changes: InputChanges, text: String): String {
    return processInput(changes, text) { change, stringBuilder ->
        stringBuilder.append(change.fixedChar.char)
    }
}

private fun extractedInput(changes: InputChanges, text: String): String {
    return processInput(changes, text) { change, stringBuilder ->
        if (change.fixedChar.extracted) {
            stringBuilder.append(change.fixedChar.char)
        }
    }
}

private fun clearedInput(changes: InputChanges, text: String): String {
    return processInput(changes, text) { _, _ -> }
}

private inline fun processInput(
    changes: InputChanges,
    text: String,
    onInsert: (InputChange.Insert, StringBuilder) -> Unit
): String {
    return buildString {
        var sourceOffset = 0
        changes.fastForEach { change ->
            when (change) {
                is InputChange.Take -> {
                    append(text.substring(sourceOffset, sourceOffset + change.chars))
                    sourceOffset += change.chars
                }
                is InputChange.Drop -> sourceOffset += change.chars
                is InputChange.Insert -> onInsert(change, this)
            }
        }
    }
}