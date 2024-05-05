package io.github.skeptick.inputmask.core

import kotlin.jvm.JvmInline

public typealias InputChanges = List<InputChange>

public sealed interface InputChange {
    @JvmInline public value class Drop(public val chars: Int) : InputChange
    @JvmInline public value class Take(public val chars: Int) : InputChange
    @JvmInline public value class Insert(public val fixedChar: InputSlot.FixedChar) : InputChange
}