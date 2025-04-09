package io.github.skeptick.inputmask.compose

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.util.fastForEach
import io.github.skeptick.inputmask.core.InputChange
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMasks
import io.github.skeptick.inputmask.core.format

@Composable
public fun rememberInputMaskInputTransformation(mask: String): InputMaskInputTransformation {
    return remember(mask) {
        InputMaskInputTransformation(mask)
    }
}

@Stable
public class InputMaskInputTransformation(private val inputMask: InputMask) : InputTransformation {

    public constructor(mask: String) : this(InputMasks.getOrCreate(mask))

    override fun TextFieldBuffer.transformInput() {
        var index = 0
        inputMask.format(this.toString(), replacePrefix = false).inputChanges.fastForEach { change ->
            when (change) {
                is InputChange.Drop -> this.delete(index, index + change.chars)
                is InputChange.Take -> index += change.chars
                is InputChange.Preserve -> index += change.chars
                is InputChange.Insert -> Unit
            }
        }
    }

}