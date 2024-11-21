package io.github.skeptick.inputmask.compose

import androidx.compose.foundation.text.input.OutputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.runtime.Stable
import androidx.compose.ui.util.fastForEach
import io.github.skeptick.inputmask.core.InputChange
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMasks
import io.github.skeptick.inputmask.core.format

@Stable
public class InputMaskOutputTransformation(private val inputMask: InputMask) : OutputTransformation {

    public constructor(mask: String) : this(InputMasks.getOrCreate(mask))

    override fun TextFieldBuffer.transformOutput() {
        var index = 0
        inputMask.format(originalText.toString(), replacePrefix = false).inputChanges.fastForEach { change ->
            when (change) {
                is InputChange.Drop -> delete(index, index + change.chars)
                is InputChange.Take -> index += change.chars
                is InputChange.Preserve -> index += change.chars
                is InputChange.Insert -> insert(index++, change.fixedChar.char.toString())
            }
        }
    }

}