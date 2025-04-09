package io.github.skeptick.inputmask.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import io.github.skeptick.inputmask.core.FormatResult
import io.github.skeptick.inputmask.core.InputChange
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMasks
import io.github.skeptick.inputmask.core.format

@Composable
public fun rememberInputMaskVisualTransformation(mask: String): InputMaskVisualTransformation {
    return remember(mask) {
        InputMaskVisualTransformation(mask)
    }
}

/**
 * A basic implementation of VisualTransformation based on InputMask.
 * It introduces an additional `sanitize` function,
 * which is necessary due to the specific behavior of VisualTransformation.
 * This function sanitizes the value entered by the user
 * (removing unnecessary characters) before formatting and saving it into the state.
 * You need to call this function manually before saving the value (typically in `onValueChange`).
 *
 * Example usage:
 * ```
 * var text by remember { mutableStateOf("") }
 * val mask = "[000]-[000]"
 * val visualTransformation = remember { InputMaskVisualTransformation(mask) }
 *
 * BasicTextField(
 *     value = text,
 *     onValueChange = { text = visualTransformation.sanitize(it) },
 *     visualTransformation = visualTransformation
 * )
 * ```
 */
@Stable
public open class InputMaskVisualTransformation(mask: String) : VisualTransformation {

    public val inputMask: InputMask = InputMasks.getOrCreate(mask)

    private var lastFormatResult: FormatResult = inputMask.format("")

    private var lastTransformedText: TransformedText = TransformedText(
        text = AnnotatedString(lastFormatResult.formattedValue),
        offsetMapping = InputMaskOffsetMapping(lastFormatResult)
    )

    public open fun sanitize(text: String): String {
        if (lastFormatResult.sourceValue == text) {
            return lastFormatResult.clearedValue
        }
        lastFormatResult = inputMask.format(text, replacePrefix = false)
        lastTransformedText = TransformedText(
            text = AnnotatedString(lastFormatResult.formattedValue),
            offsetMapping = InputMaskOffsetMapping(lastFormatResult)
        )
        return lastFormatResult.clearedValue
    }

    override fun filter(text: AnnotatedString): TransformedText {
        if (text.text == lastFormatResult.clearedValue) {
            return lastTransformedText
        }
        lastFormatResult = inputMask.format(text.text, replacePrefix = false)
        lastTransformedText = TransformedText(
            text = AnnotatedString(lastFormatResult.formattedValue),
            offsetMapping = InputMaskOffsetMapping(lastFormatResult)
        )
        return lastTransformedText
    }

    private class InputMaskOffsetMapping(val formatResult: FormatResult) : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            var addedChars = 0
            var remainingOffset = offset
            for (change in formatResult.inputChanges) when {
                remainingOffset < 0 -> break
                change is InputChange.Insert -> addedChars += 1
                remainingOffset == 0 -> break
                change is InputChange.Take -> remainingOffset -= change.chars
                change is InputChange.Preserve -> remainingOffset -= change.chars
            }
            return offset + addedChars
        }

        override fun transformedToOriginal(offset: Int): Int {
            var addedChars = 0
            var remainingOffset = offset
            for (change in formatResult.inputChanges) when {
                remainingOffset - addedChars <= 0 -> break
                change is InputChange.Insert -> addedChars += 1
                change is InputChange.Take -> remainingOffset -= change.chars
                change is InputChange.Preserve -> remainingOffset -= change.chars
            }
            return offset - addedChars
        }

    }

}