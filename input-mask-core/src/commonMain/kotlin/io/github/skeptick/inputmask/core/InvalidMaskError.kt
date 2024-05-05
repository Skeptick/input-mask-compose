package io.github.skeptick.inputmask.core

public sealed class InvalidMaskError(override val message: String) : Exception() {

    public class UnexpectedCharInInput(char: Char) : InvalidMaskError(
        "Invalid mask. '$char' inside [] isn't allowed."
    )

    public class UnexpectedCharInExtraction(char: Char) : InvalidMaskError(
        "Invalid mask. '$char' inside {} isn't allowed. Use escaping."
    )

    public class UnexpectedInputClosing : InvalidMaskError(
        "Invalid mask. ']' allowed only after '[' or must be escaped."
    )

    public class UnexpectedExtractionClosing : InvalidMaskError(
        "Invalid mask. '}' allowed only after '{' or must be escaped."
    )

    public class DuplicateInfiniteSlot(slotType: String) : InvalidMaskError(
        "Invalid mask. Infinite $slotType after infinite symbols is pointless."
    )

    public class SlotAfterInfiniteSlot(symbolType: String) : InvalidMaskError(
        "Invalid mask. ${symbolType.replaceFirstChar { it.uppercase() }} after infinite symbols is pointless."
    )

    public class RequiredSlotAfterOptionalSlot(slotType: String) : InvalidMaskError(
        "Invalid mask. Required $slotType after optional $slotType is not allowed."
    )

}