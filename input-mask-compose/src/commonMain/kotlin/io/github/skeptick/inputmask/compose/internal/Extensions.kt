package io.github.skeptick.inputmask.compose.internal

import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputSlot
import io.github.skeptick.inputmask.core.isDigit

internal val InputMask.inputDigits: Int
    get() = slots.fastFold(0) { acc, slot ->
        if (slot.isDigit) acc + 1 else acc
    }

internal val InputMask.extractedPrefix: String
    get() = buildString {
        slots.fastForEach { slot ->
            when (slot) {
                is InputSlot.FixedChar -> if (slot.extracted) append(slot.char)
                else -> return@buildString
            }
        }
    }