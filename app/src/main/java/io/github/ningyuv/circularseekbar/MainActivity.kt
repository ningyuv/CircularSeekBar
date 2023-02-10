package io.github.ningyuv.circularseekbar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ningyuv.circularseekbar.ui.theme.CircularSeekbarTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CircularSeekbarTheme {
                var value by rememberSaveable {
                    mutableStateOf(0f)
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DemoScreen(
                        value = value,
                        onChange = { v -> value = v }
                    )
                }
            }
        }
    }
}

@Composable
fun DemoScreen(
    value: Float,
    onChange: (Float) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularSeekbarView(
            value = value,
            onChange = onChange,
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(152.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            item {
                CircularSeekbarView(
                    value = value, onChange = onChange,
                    startAngle = -120f, fullAngle = 240f,
                    drawDot = { dotCenter, angle, color, radius ->
                        val starPath = Path()
                        val n = 8
                        starPath.moveTo(dotCenter.x + 0f, dotCenter.y - radius)
                        for (i in 0 until n) {
                            val rad1 = PI * 2 / (n * 2) * (i * 2 + 1) - PI / 2
                            val offset1 = Offset(
                                (dotCenter.x + cos(rad1) * radius * cos(PI / n)).toFloat(),
                                (dotCenter.y + sin(rad1) * radius * cos(PI / n)).toFloat()
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
            }
            item {
                CircularSeekbarView(
                    value = value, onChange = onChange,
                    fullAngle = 270f,
                    drawDot = { dotCenter, angle, color, radius ->
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
            }
        }
        Text(text = "progress: $value", Modifier.padding(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            val num = 2
            for (i in 0..num) {
                val progress = 1f / num * i
                Button(onClick = { onChange(progress) }) {
                    Text(text = "set $progress")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CircularSeekbarTheme {
        DemoScreen(
            value = 0.5f,
            onChange = {},
        )
    }
}