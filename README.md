# input-mask-compose

Библиотека для форматирования вводимых пользователем значений с поддержкой Compose Multiplatform.

## Использование

```kotlin
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("io.github.skeptick.inputmask:inputmask-core:0.0.5")
                implementation("io.github.skeptick.inputmask:inputmask-compose:0.0.5")
            }
        }
    }
}
```

## Поддерживаемые платформы

`core` поддерживает все таргеты Kotlin, `compose` все таргеты Compose Multiplatform.

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

Артефакт `compose` поставляет имлементации `VisualTransformation` для `TextField` принимающих `String` или `TextFieldValue`:

```kotlin
var text by remember { mutableStateOf("") }
val mask = "[000]-[000]"
val visualTransformation = remember { InputMaskVisualTransformation(mask) }

BasicTextField(
    value = text,
    onValueChange = { text = visualTransformation.sanitize(it) },
    visualTransformation = visualTransformation,
)
```

Специальная вариация для форматирования телефона, обрабатывающая вставку в поле ввода номера как
с кодом страны, так и без:

```kotlin
var text by remember { mutableStateOf("") }
var mask = "+{7} ([000]) [000]-[0000]"
val visualTransformation = remember(mask) { PhoneInputMaskVisualTransformation(mask) }

BasicTextField(
    value = value,
    onValueChange = { text = visualTransformation.sanitize(it) },
    visualTransformation = visualTransformation,
)
```

Обратите внимание, что если маска может измениться в процессе ввода (например, пользователю предоставляется
возможность выбрать страну во время авторизации), то нужно также обновить значение:

```kotlin
BasicTextField(
    value = remember(text, mask) { visualTransformation.sanitize(value) },
    // ...
)
```

Для полей ввода, построенных вокруг `TextFieldState` есть имплементации `InputTransformation` и `OutputTransformation`:

```kotlin
val textFieldState = remember { TextFieldState() }
val mask = "[000]-[000]"
    
BasicTextField(
    state = textFieldState,
    inputTransformation = remember(mask) { InputMaskInputTransformation(mask) },
    outputTransformation = remember(mask) { InputMaskOutputTransformation(mask) },
)
```

И специальная вариация `InputTransformation` для телефонных номеров:

```kotlin
BasicTextField(
    // ...
    inputTransformation = remember(mask) { PhoneInputMaskInputTransformation(mask) },
    // ...
)
```