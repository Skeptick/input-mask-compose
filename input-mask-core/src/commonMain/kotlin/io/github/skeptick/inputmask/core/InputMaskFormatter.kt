@file:Suppress("FunctionName")

package io.github.skeptick.inputmask.core

import io.github.skeptick.inputmask.core.internal.fastForEach
import io.github.skeptick.inputmask.core.internal.fastLazy
import io.github.skeptick.inputmask.core.internal.process

/**
 * @property[sourceValue] Original string for which formatting was performed
 * @property[inputChanges] Changes that need to be sequentially applied to [sourceValue]
 * @property[isComplete] `true` if result matches the mask (all required characters are filled)
 */
@ConsistentCopyVisibility
public data class FormatResult internal constructor(
    val sourceValue: String,
    val inputChanges: InputChanges,
    val isComplete: Boolean
) {

    /**
     * Formatted value
     * Mask = +{7} ([000]) [000]-[0000]
     * [sourceValue] = 123abc456def7890
     * [formattedValue] = +7 (123) 456-7890
     */
    val formattedValue: String by fastLazy { formattedInput(inputChanges, sourceValue) }

    /**
     * Extracted value
     * Mask = +{7} ([000]) [000]-[0000]
     * [sourceValue] = 123abc456def7890
     * [extractedValue] = 71234567890
     */
    val extractedValue: String by fastLazy { extractedInput(inputChanges, sourceValue) }

    /**
     * Cleared value. Used during input to clear user input. It has no purpose beyond input fields
     * Mask = +{7} ([000]) [000]-[0000]
     * [sourceValue] = 123abc456def7890
     * [clearedValue] = 1234567890
     */
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
    return processInput(changes, text, appendPreserve = true) { change, stringBuilder ->
        stringBuilder.append(change.fixedChar.char)
    }
}

private fun extractedInput(changes: InputChanges, text: String): String {
    return processInput(changes, text, appendPreserve = false) { change, stringBuilder ->
        if (change.fixedChar.extracted) {
            stringBuilder.append(change.fixedChar.char)
        }
    }
}

private fun clearedInput(changes: InputChanges, text: String): String {
    return processInput(changes, text, appendPreserve = true) { _, _ -> }
}

private inline fun processInput(
    changes: InputChanges,
    text: String,
    appendPreserve: Boolean,
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
                is InputChange.Preserve -> {
                    if (appendPreserve) append(text.substring(sourceOffset, sourceOffset + change.chars))
                    sourceOffset += change.chars
                }
                is InputChange.Drop -> sourceOffset += change.chars
                is InputChange.Insert -> onInsert(change, this)
            }
        }
    }
}