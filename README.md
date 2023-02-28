# CircularSeekBar
A circular SeekBar with Jetpack Compose

## Demo app
[demo app](https://github.com/ningyuv/CircularSeekBar/releases/latest/download/CircularSeekBar-demo.apk) in release

<img src="https://user-images.githubusercontent.com/25382292/221898250-b8f2ed98-e299-46a6-8eb6-5785cecae4d0.png" alt="" width="320">

## Setup
### Gradle
``` Gradle
dependencies {
    implementation 'io.github.ningyuv:circular-seek-bar:0.0.3'
}
```

## Usage
### basic
```kt
var value by rememberSaveable {
    mutableStateOf(0f)
}
CircularSeekbarView(
    value = value,
    onChange = { v -> value = v }
)
```
### set value
```kt
var value by rememberSaveable {
    mutableStateOf(0f)
}
CircularSeekbarView(
    value = value,
    onChange = { v -> value = v }
)
Button(onClick = { value = 0.5f }) {
    Text(text = "set 0.5f")
}
```
### custom line weight and dot size
```kt
var value by rememberSaveable {
    mutableStateOf(0f)
}
CircularSeekbarView(
    value = value,
    onChange = { v -> value = v },
    lineWeight = 10.dp,
    dotRadius = 10.dp
)
```
### custom colors
```kt
var value by rememberSaveable {
    mutableStateOf(0f)
}
CircularSeekbarView(
    value = value,
    onChange = { v -> value = v },
    activeColor = MaterialTheme.colorScheme.onPrimaryContainer,
    inactiveColor = MaterialTheme.colorScheme.primaryContainer,
    dotColor = MaterialTheme.colorScheme.primary
)
```
### custom angle and drag dot
```kt
CircularSeekbarView(
    value = value, onChange = onChange,
    startAngle = -120f, fullAngle = 240f,
    drawDot = { dotCenter, angle, color, radius ->
      // draw a regular star polygon as drag dot
      val starPath = Path()
      val n = 8
      starPath.moveTo(dotCenter.x + 0f, dotCenter.y - radius)
      for (i in 0 until n) {
          val rad1 = PI * 2 / (n * 2) * (i * 2 + 1) - PI / 2
          val offset1 = Offset(
              (dotCenter.x + cos(rad1) * radius * cos(PI * 2 / n) / cos(PI / n)).toFloat(),
              (dotCenter.y + sin(rad1) * radius * cos(PI * 2 / n) / cos(PI / n)).toFloat()
          )
          starPath.lineTo(offset1.x, offset1.y)
          val rad2 = PI * 2 / (n * 2) * (i * 2 + 2) - PI / 2
          val offset2 = Offset(
              (dotCenter.x + cos(rad2) * radius).toFloat(),
              (dotCenter.y + sin(rad2) * radius).toFloat()
          )
          starPath.lineTo(offset2.x, offset2.y)
      }
      rotate(angle + 90f, dotCenter) {
          drawPath(starPath, color)
      }
  }
)
```
### steps
```kt
var value by rememberSaveable {
    mutableStateOf(0f)
}
CircularSeekbarView(
    value = value,
    onChange = { v -> value = v },
    steps = 10
)
```
