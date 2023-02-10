package io.github.ningyuv.circularseekbar

import android.content.res.Resources.getSystem
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.RequestDisallowInterceptTouchEvent
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.*

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CircularSeekbarView(
    value: Float,
    onChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    steps: Int = 0,
    startAngle: Float = 0f,
    fullAngle: Float = 360f,
    activeColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    inactiveColor: Color = MaterialTheme.colorScheme.primaryContainer,
    dotColor: Color = MaterialTheme.colorScheme.primary,
    lineWeight: Dp = 20.dp,
    dotRadius: Dp = 20.dp,
    dotTouchThreshold: Dp = 15.dp,
    drawInCircle: DrawScope.() -> Unit = {},
    drawDot: (DrawScope.(Offset, Float, Color, Float) -> Unit)? = null
) {
    val dpScale = getSystem().displayMetrics.density
    val lineWeightInPx = dpScale * lineWeight.value
    val dotRadiusInPx = dpScale * dotRadius.value
    val dotTouchThresholdInPx = dpScale * dotTouchThreshold.value
    val innerStartAngle = startAngle - 90f
    val stroke = Stroke(lineWeightInPx)
    var sweepAngle = value * fullAngle
    if (steps > 0) {
        val perStep = fullAngle / steps
        sweepAngle = (sweepAngle / perStep).roundToInt() * perStep
    }
    var dragCenter by remember {
        mutableStateOf<Offset?>(null)
    }
    var arcCenter by remember {
        mutableStateOf<Offset?>(null)
    }
    var dragging by remember {
        mutableStateOf(false)
    }
    val requestDisallowInterceptTouchEvent = remember {
        RequestDisallowInterceptTouchEvent()
    }
    Canvas(modifier = modifier
        .fillMinDimension()
        .pointerInteropFilter(
            requestDisallowInterceptTouchEvent
        ) {
            when (it.action) {
                MotionEvent.ACTION_DOWN -> {
                    val center = dragCenter ?: return@pointerInteropFilter false
                    if (Offset(it.x, it.y)
                            .minus(center)
                            .getDistance() <= dotRadiusInPx + dotTouchThresholdInPx
                    ) {
                        dragging = true
                        requestDisallowInterceptTouchEvent(true)
                    }
                }
                MotionEvent.ACTION_MOVE -> {
                    if (dragging) {
                        val center = arcCenter ?: return@pointerInteropFilter false
                        val v = Offset(it.x, it.y).minus(center)
                        if (v.getDistance() == 0f) return@pointerInteropFilter true
                        var nextSweepAngle =
                            (atan(v.y / v.x) / PI * 180 - innerStartAngle).toFloat()
                        if (v.x < 0) {
                            nextSweepAngle += 180
                        }
                        while (nextSweepAngle < 0) {
                            nextSweepAngle += 360
                        }
                        while (nextSweepAngle > 360) {
                            nextSweepAngle -= 360
                        }
                        nextSweepAngle =
                            if (sweepAngle > fullAngle * 3 / 4 && (nextSweepAngle < fullAngle / 2 || nextSweepAngle > fullAngle)) {
                                fullAngle
                            } else if (sweepAngle < fullAngle * 1 / 4 && nextSweepAngle > fullAngle / 2) {
                                0f
                            } else {
                                nextSweepAngle
                            }
                        if (steps > 0) {
                            val perStep = fullAngle / steps
                            val step = (nextSweepAngle / perStep).roundToInt()
                            nextSweepAngle = step * perStep
                        }
                        if (nextSweepAngle / fullAngle != value) {
                            onChange(nextSweepAngle / fullAngle)
                        }
                    }
                }
                MotionEvent.ACTION_UP -> {
                    dragging = false
                    requestDisallowInterceptTouchEvent(false)
                }
                MotionEvent.ACTION_CANCEL -> {
                    dragging = false
                }
                else -> return@pointerInteropFilter false
            }
            true
        }) {
        val radius = size.minDimension / 2.0f - max(lineWeightInPx / 2, dotRadiusInPx)
        drawArc(
            color = activeColor,
            startAngle = innerStartAngle,
            sweepAngle = sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = stroke
        )
        drawArc(
            color = inactiveColor,
            startAngle = innerStartAngle + sweepAngle,
            sweepAngle = fullAngle - sweepAngle,
            useCenter = false,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2, radius * 2),
            style = stroke
        )
        val dotAngle = (innerStartAngle + sweepAngle)
        val dotCenter = Offset(
            center.x + cos((dotAngle / 180f * PI).toFloat()) * radius,
            center.y + sin((dotAngle / 180f * PI).toFloat()) * radius
        )
        this.drawInCircle()
        if (drawDot != null) {
            this.drawDot(dotCenter, dotAngle, dotColor, dotRadiusInPx)
        } else {
            drawCircle(
                color = dotColor,
                center = dotCenter,
                radius = dotRadiusInPx
            )
        }
        dragCenter = dotCenter
        arcCenter = center
    }
}

@Preview(showBackground = true)
@Composable
fun CircularSeekbarPreview() {
    CircularSeekbarView(
        value = 0.45f, onChange = {}
    )
}