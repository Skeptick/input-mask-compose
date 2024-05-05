package io.github.skeptick.inputmask.core.internal

internal inline fun <T> fastLazy(noinline initializer: () -> T): Lazy<T> {
    return lazy(LazyThreadSafetyMode.NONE, initializer)
}

internal inline fun <T> List<T>.fastForEach(action: (T) -> Unit) {
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}

internal inline fun CharSequence.fastForEach(action: (Char) -> Unit) {
    for (index in indices) {
        val item = get(index)
        action(item)
    }
}

internal inline fun <R> CharSequence.fastFold(initial: R, operation: (acc: R, Char) -> R): R {
    var accumulator = initial
    fastForEach { accumulator = operation(accumulator, it) }
    return accumulator
}

internal inline fun CharSequence.countWhileNot(predicate: (Char) -> Boolean): Int {
    return countWhile { !predicate(it) }
}

internal inline fun CharSequence.countWhile(predicate: (Char) -> Boolean): Int {
    return fastFold(initial = 0) { acc, char ->
        val isValid = predicate(char)
        if (!isValid) return acc else acc + 1
    }
}

internal inline fun CharSequence.isCharValidAt(index: Int, predicate: (Char) -> Boolean): Boolean {
    return getOrNull(index)?.let(predicate) == true
}