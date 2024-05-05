@file:Suppress("UnusedReceiverParameter")

package io.github.skeptick.inputmask.core.internal

import io.github.skeptick.inputmask.core.DefaultInputMaskBuilder
import io.github.skeptick.inputmask.core.InputMask
import io.github.skeptick.inputmask.core.InputMaskBuilder
import io.github.skeptick.inputmask.core.InputMasks
import io.github.skeptick.inputmask.core.InputSlot
import io.github.skeptick.inputmask.core.InvalidMaskError

internal fun parseInputMask(mask: String) = InputMasks.buildInternal {
    for (char in mask) when (char) {
        Tokens.ESCAPE -> when {
            isInput -> throw InvalidMaskError.UnexpectedCharInInput(char)
            isEscaped -> fixedChar(char, extracted = isExtraction)
            else -> isEscaped = true
        }
        Tokens.USER_INPUT_START -> when {
            isEscaped -> fixedChar(char, extracted = isExtraction)
            isInput -> throw InvalidMaskError.UnexpectedCharInInput(char)
            isExtraction -> throw InvalidMaskError.UnexpectedCharInExtraction(char)
            else -> isInput = true
        }
        Tokens.USER_INPUT_END -> when {
            isEscaped -> fixedChar(char, extracted = isExtraction)
            isInput -> isInput = false
            isExtraction -> throw InvalidMaskError.UnexpectedCharInExtraction(char)
            else -> throw InvalidMaskError.UnexpectedInputClosing()
        }
        Tokens.EXTRACT_INCLUSION_START -> when {
            isEscaped -> fixedChar(char, extracted = isExtraction)
            isInput -> throw InvalidMaskError.UnexpectedCharInInput(char)
            isExtraction -> throw InvalidMaskError.UnexpectedCharInExtraction(char)
            else -> isExtraction = true
        }
        Tokens.EXTRACT_INCLUSION_END -> when {
            isEscaped -> fixedChar(char, extracted = isExtraction)
            isInput -> throw InvalidMaskError.UnexpectedCharInInput(char)
            isExtraction -> isExtraction = false
            else -> throw InvalidMaskError.UnexpectedExtractionClosing()
        }
        Tokens.REQUIRED_DIGIT -> when {
            isInput -> singleDigit(required = true)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.REQUIRED_LETTER -> when {
            isInput -> singleLetter(required = true)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.REQUIRED_SYMBOL -> when {
            isInput -> singleDigitOrLetter(required = true)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.OPTIONAL_DIGIT -> when {
            isInput -> singleDigit(required = false)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.OPTIONAL_LETTER -> when {
            isInput -> singleLetter(required = false)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.OPTIONAL_SYMBOL -> when {
            isInput -> singleDigitOrLetter(required = false)
            else -> fixedChar(char, extracted = isExtraction)
        }
        Tokens.INFINITE_SYMBOLS -> when {
            isInput -> when (currentInput.lastOrNull()) {
                InputSlot.RequiredDigit, InputSlot.OptionalDigit -> digits()
                InputSlot.RequiredLetter, InputSlot.OptionalLetter -> letters()
                else -> digitsOrLetters()
            }
            else -> fixedChar(char, extracted = isExtraction)
        }
        else -> when {
            isInput -> throw InvalidMaskError.UnexpectedCharInInput(char)
            else -> fixedChar(char, extracted = isExtraction)
        }
    }
}

internal fun InputMasks.buildInternal(builder: StatefulInputMaskBuilder.() -> Unit): InputMask {
    return InputMask(InternalInputMaskBuilder().apply(builder).slots)
}

internal interface StatefulInputMaskBuilder : InputMaskBuilder {
    val currentInput: List<InputSlot>
    var isInput: Boolean
    var isExtraction: Boolean
    var isEscaped: Boolean
}

internal class InternalInputMaskBuilder : DefaultInputMaskBuilder(), StatefulInputMaskBuilder {

    override val currentInput: MutableList<InputSlot> = mutableListOf()

    override var isInput: Boolean = false
        set(value) {
            field = value
            currentInput.clear()
        }

    override var isExtraction: Boolean = false

    override var isEscaped: Boolean = false

    override operator fun plusAssign(slot: InputSlot) {
        super.plusAssign(slot)
        when (slot) {
            is InputSlot.FixedChar -> isEscaped = false
            else -> currentInput += slot
        }
    }

}

private object Tokens {
    const val ESCAPE = '\\'
    const val EXTRACT_INCLUSION_START = '{'
    const val EXTRACT_INCLUSION_END = '}'
    const val USER_INPUT_START = '['
    const val USER_INPUT_END = ']'
    const val REQUIRED_DIGIT = '0'
    const val OPTIONAL_DIGIT = '9'
    const val REQUIRED_LETTER = 'A'
    const val OPTIONAL_LETTER = 'a'
    const val REQUIRED_SYMBOL = '_'
    const val OPTIONAL_SYMBOL = '-'
    const val INFINITE_SYMBOLS = '…'
}