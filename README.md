# input-mask-compose

Библиотека для форматирования вводимых пользователем значений с поддержкой Compose Multiplatform.

## Использование

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.skeptick.inputmask:core:0.0.2")
                implementation("io.github.skeptick.inputmask:compose:0.0.2")
            }
        }
    }
}
```

## Поддерживаемые платформы

`core` поддерживает все таргеты Kotlin-а, `compose` все таргеты Compose Multiplatform.

## Идея

Синтаксис масок вдохновлён небезызвестной библиотекой от RedMadRobot (
[Android](https://github.com/RedMadRobot/input-mask-android),
[iOS](https://github.com/RedMadRobot/input-mask-ios)
), но реализация гораздо более примитивная. В частности, не поддерживаются
[«родственные» маски](https://github.com/RedMadRobot/input-mask-android/wiki/2.1-Affine-Masks).

## Объявление масок

Детальнее [в документации RedMadRobot](https://github.com/RedMadRobot/input-mask-android/wiki/Mask-Syntax:-Basics).  

Верхнеуровнево:
- в `[]` описываем то, что ожидаем от пользователя
- в `{}` любые символы, которые хотим получить в извлекаемом значении
- все символы за пределами `[]` и `{}` будут вставлены в процессе форматирования, но не попадут в извлекаемое значение

В `[]` поддерживаются следующие символы:
- `0` - обязательная цифра
- `9` - опциональная цифра
- `A` - обязательная буква
- `a` - опциональная буква
- `_` - обязательная буква или цифра
- `-` - опциональная буква или цифра
- `…` - неограниченное количество цифр или букв
  - если перед `…` стоит `0` или `9`, то будут ожидаться цифровые символы
  - если перед `…` стоит `A` или `a`, то будут ожидаться буквенные символы
  - если перед `…` стоит `_` или `-`, или не стоит ничего, то будут ожидаться буквы или цифры

### Создание маски

```kotlin
val inputMask = InputMasks.getOrCreate("+{7} ([000]) [000]-[0000]")
```

Поддерживается создание с помощью простого DSL:
```kotlin
val inputMask = InputMasks.build {
    fixedChar('+', extracted = false)
    fixedChar('7', extracted = true)
    fixedChar(' ', extracted = false)
    fixedChar('(', extracted = false)
    repeat(3) { singleDigit(required = true) }
    fixedChar(')', extracted = false)
    fixedChar(' ', extracted = false)
    repeat(3) { singleDigit(required = true) }
    fixedChar('-', extracted = false)
    repeat(4) { singleDigit(required = true) }
}

```
### Использование

```kotlin
val result = inputMask.format("9001234567")
result.isComplete // -> true
result.formattedValue // -> +7 (900) 123-4567
result.extractedValue // -> 79001234567
```

Обратите внимание на параметр `replacePrefix`:

```kotlin
inputMask.format("79001234567", replacePrefix = true) // -> +7 (900) 123-4567
inputMask.format("79001234567", replacePrefix = false) // -> +7 (790) 012-3456
```

### Использование в Compose

Артефакт `compose` поставляет базовые `MaskedTextField` и `MaskedOutlinedTextField`.  
Вы также можете использовать свои, вместе с идущим в комплекте `InputMaskVisualTransformation`:

```kotlin
val mask = "+{7} ([000]) [000]-[0000]"
val visualTransformation = remember(mask) { InputMaskVisualTransformation(mask) }
var value by remember { mutableStateOf("") }

TextField(
    value = value,
    onValueChange = { value = visualTransformation.clear(it) },
    visualTransformation = visualTransformation
)
```