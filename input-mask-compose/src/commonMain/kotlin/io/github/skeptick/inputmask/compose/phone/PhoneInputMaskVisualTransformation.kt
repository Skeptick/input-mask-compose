package io.github.skeptick.inputmask.compose.phone

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.skeptick.inputmask.compose.InputMaskVisualTransformation
import io.github.skeptick.inputmask.compose.internal.extractedPrefix
import io.github.skeptick.inputmask.compose.internal.inputDigits

/**
 * A specialized implementation of VisualTransformation for formatting phone numbers.
 * Unlike the standard [InputMaskVisualTransformation], this implementation can correctly handle
 * the insertion of phone numbers both with and without a country code.
 *
 * (!) Note: If the mask can change during input (e.g., when allowing the user to select a country),
 * you also need to process the TextField value accordingly.
 *
 * Example usage:
 * ```
 * var text by remember { mutableStateOf("") }
 * var mask by remember { mutableStateOf("+{7} ([000]) [000]-[00]-[00]") }
 * val visualTransformation = rememberPhoneInputMaskVisualTransformation(mask)
 *
 * BasicTextField(
 *     value = remember(text, mask) { visualTransformation.sanitize(value) },
 *     onValueChange = { text = visualTransformation.sanitize(it) },
 *     visualTransformation = visualTransformation,
 * )
 * ```
 */
@Composable
public fun rememberPhoneInputMaskVisualTransformation(mask: String): InputMaskVisualTransformation {
    return remember(mask) {
        @Suppress("DEPRECATION")
        PhoneInputMaskVisualTransformation(mask)
    }
}

@Stable
public class PhoneInputMaskVisualTransformation @Deprecated(
    message = "Use `rememberPhoneInputMaskVisualTransformation`",
    replaceWith = ReplaceWith("rememberPhoneInputMaskVisualTransformation(mask)")
) constructor(mask: String) : InputMaskVisualTransformation(mask) {

    private val maskExtractedPrefix by lazy(LazyThreadSafetyMode.NONE) { inputMask.extractedPrefix }
    private val maskDigitsCount by lazy(LazyThreadSafetyMode.NONE) { inputMask.inputDigits }

    override fun sanitize(text: String): String {
        val digits = text.filter(Char::isDigit)
        val maxLength = maskDigitsCount + maskExtractedPrefix.length
        return super.sanitize(
            when {
                digits.length != maxLength || !digits.startsWith(maskExtractedPrefix) -> text
                else -> digits.substringAfter(maskExtractedPrefix)
            }
        )
    }

}