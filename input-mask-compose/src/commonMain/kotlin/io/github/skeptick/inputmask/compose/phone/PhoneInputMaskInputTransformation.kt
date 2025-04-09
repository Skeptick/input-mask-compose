package io.github.skeptick.inputmask.compose.phone

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.then
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import io.github.skeptick.inputmask.compose.InputMaskInputTransformation
import io.github.skeptick.inputmask.compose.internal.extractedPrefix
import io.github.skeptick.inputmask.compose.internal.inputDigits
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMasks

/**
 * A specialized implementation of InputTransformation for formatting phone numbers.
 * Unlike the standard [InputMaskInputTransformation], this implementation can correctly handle
 * the insertion of phone numbers both with and without a country code.
 *
 * Example usage:
 * ```
 * val textFieldState = remember { TextFieldState() }
 * val mask = "+{7} ([000]) [000]-[00]-[00]"
 *
 * BasicTextField(
 *     state = textFieldState,
 *     inputTransformation = rememberPhoneInputMaskInputTransformation(mask),
 *     outputTransformation = rememberInputMaskOutputTransformation(mask),
 * )
 * ```
 */
@Composable
public fun rememberPhoneInputMaskInputTransformation(mask: String): InputTransformation {
    return remember(mask) {
        val inputMask = InputMasks.getOrCreate(mask)
        PhoneInputMaskInputTransformation(inputMask).then(InputMaskInputTransformation(inputMask))
    }
}

@Deprecated(
    message = "Use `rememberPhoneInputMaskInputTransformation`",
    replaceWith = ReplaceWith("rememberPhoneInputMaskInputTransformation(mask)")
)
@Composable
public fun PhoneInputMaskInputTransformation(mask: String): InputTransformation {
    return remember(mask) {
        val inputMask = InputMasks.getOrCreate(mask)
        PhoneInputMaskInputTransformation(inputMask).then(InputMaskInputTransformation(inputMask))
    }
}

@Stable
private class PhoneInputMaskInputTransformation(inputMask: InputMask) : InputTransformation {

    private val maskExtractedPrefix by lazy(LazyThreadSafetyMode.NONE) { inputMask.extractedPrefix }
    private val maskDigitsCount by lazy(LazyThreadSafetyMode.NONE) { inputMask.inputDigits }

    override fun TextFieldBuffer.transformInput() {
        val digits = toString().filter(Char::isDigit)
        val maxLength = maskDigitsCount + maskExtractedPrefix.length
        if (digits.length == maxLength && digits.startsWith(maskExtractedPrefix)) delete(0, maskExtractedPrefix.length)
    }

}