package io.github.skeptick.inputmask.core

import kotlin.jvm.JvmInline

/**
 * Changes that need to be sequentially applied to the input string
 */
public typealias InputChanges = List<InputChange>

public sealed interface InputChange {

    /**
     * Number of characters to drop
     */
    @JvmInline public value class Drop(public val chars: Int) : InputChange

    /**
     * Number of characters to take
     */
    @JvmInline public value class Take(public val chars: Int) : InputChange

    /**
     * Number of characters that need to be taken during input, but that should not be in the extracted value
     */
    @JvmInline public value class Preserve(public val chars: Int) : InputChange

    /**
     * Character to insert
     */
    @JvmInline public value class Insert(public val fixedChar: InputSlot.FixedChar) : InputChange

}