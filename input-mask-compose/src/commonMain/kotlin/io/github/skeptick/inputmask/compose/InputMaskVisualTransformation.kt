package io.github.skeptick.inputmask.compose

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMasks
import io.github.skeptick.inputmask.core.InputSlot
import io.github.skeptick.inputmask.core.clear
import io.github.skeptick.inputmask.core.format
import io.github.skeptick.inputmask.core.isDigit
import io.github.skeptick.inputmask.core.isInfinite

public class InputMaskVisualTransformation(mask: String) : VisualTransformation {

    public val inputMask: InputMask = InputMasks.getOrCreate(mask)

    public val keyboardType: KeyboardType = when {
        inputMask.slots.all { it.isDigit || it is InputSlot.FixedChar } -> KeyboardType.Number
        else -> KeyboardType.Text
    }

    public fun clear(text: String): String {
        return inputMask.clear(text)
    }

    override fun filter(text: AnnotatedString): TransformedText {
        return TransformedText(
            text = AnnotatedString(inputMask.format(text.toString()).value),
            offsetMapping = offsetMapping
        )
    }

    private val offsetMapping = object : OffsetMapping {

        override fun originalToTransformed(offset: Int): Int {
            var fixedChars = 0
            var remainingOffset = offset
            for (slot in inputMask.slots) when {
                slot is InputSlot.FixedChar -> fixedChars++
                remainingOffset == 0 -> break
                else -> remainingOffset -= if (slot.isInfinite) remainingOffset else 1
            }
            return offset + fixedChars
        }

        override fun transformedToOriginal(offset: Int): Int {
            var fixedChars = 0
            var remainingOffset = offset
            for (slot in inputMask.slots) when {
                remainingOffset - fixedChars == 0 -> break
                slot is InputSlot.FixedChar -> fixedChars++
                else -> remainingOffset -= if (slot.isInfinite) remainingOffset else 1
            }
            return offset - fixedChars
        }

    }

}