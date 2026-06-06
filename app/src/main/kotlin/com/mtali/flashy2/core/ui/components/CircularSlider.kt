package com.mtali.flashy2.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mtali.flashy2.core.ui.theme.FlashyAmber
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private const val START_ANGLE = 135f // bottom-left
private const val SWEEP_ANGLE = 270f // gap of 90 degrees at the bottom

/**
 * A circular arc slider with a 90-degree gap at the bottom, replacing the third-party
 * `me.tankery.lib:circularSeekBar`. [value] is normalized to `0f..1f`. The [content] slot is
 * centered inside the ring (used for the power button).
 */
@Composable
fun CircularSlider(
  value: Float,
  onValueChange: (Float) -> Unit,
  modifier: Modifier = Modifier,
  onValueChangeFinished: (() -> Unit)? = null,
  enabled: Boolean = true,
  trackColor: Color = Color(0xFFF3F3F7),
  progressColor: Color = FlashyAmber,
  thumbColor: Color = FlashyAmber,
  strokeWidth: Dp = 16.dp,
  thumbRadius: Dp = 14.dp,
  content: @Composable BoxScope.() -> Unit = {},
) {
  var layoutSize by remember { mutableStateOf(Size.Zero) }

  // The gesture pipeline below is keyed on Unit, so it captures these lambdas only once. Route
  // through rememberUpdatedState so a mode switch (e.g. torch -> screen) is always seen by the
  // live drag handler instead of the lambda from first composition.
  val currentOnValueChange by rememberUpdatedState(onValueChange)
  val currentOnValueChangeFinished by rememberUpdatedState(onValueChangeFinished)

  fun positionToValue(position: Offset): Float {
    val center = Offset(layoutSize.width / 2f, layoutSize.height / 2f)
    var angle = Math.toDegrees(atan2((position.y - center.y).toDouble(), (position.x - center.x).toDouble())).toFloat()
    if (angle < 0) angle += 360f // 0..360, where 90 = bottom
    var a = angle
    if (a < START_ANGLE) a += 360f
    return when {
      // In the bottom gap: snap to whichever end is closer.
      a > START_ANGLE + SWEEP_ANGLE -> if (a - (START_ANGLE + SWEEP_ANGLE) < (360f - SWEEP_ANGLE) / 2f) 1f else 0f
      else -> ((a - START_ANGLE) / SWEEP_ANGLE).coerceIn(0f, 1f)
    }
  }

  Box(modifier = modifier, contentAlignment = Alignment.Center) {
    Canvas(
      modifier =
      Modifier
        .matchParentSize()
        .then(
          if (enabled) {
            Modifier.pointerInput(Unit) {
              detectDragGestures(
                onDragStart = { offset -> currentOnValueChange(positionToValue(offset)) },
                onDragEnd = { currentOnValueChangeFinished?.invoke() },
              ) { change, _ -> currentOnValueChange(positionToValue(change.position)) }
            }
          } else {
            Modifier
          },
        ),
    ) {
      layoutSize = size
      val stroke = strokeWidth.toPx()
      val inset = maxOf(stroke, thumbRadius.toPx() * 2)
      val arcSize = Size(size.width - inset, size.height - inset)
      val topLeft = Offset(inset / 2f, inset / 2f)
      val clamped = value.coerceIn(0f, 1f)

      drawArc(
        color = trackColor,
        startAngle = START_ANGLE,
        sweepAngle = SWEEP_ANGLE,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
      )
      drawArc(
        color = progressColor,
        startAngle = START_ANGLE,
        sweepAngle = SWEEP_ANGLE * clamped,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(width = stroke, cap = StrokeCap.Round),
      )
      val thumbAngleRad = Math.toRadians((START_ANGLE + SWEEP_ANGLE * clamped).toDouble())
      val radius = arcSize.width / 2f
      val center = Offset(size.width / 2f, size.height / 2f)
      val thumbCenter =
        Offset(
          x = center.x + (radius * cos(thumbAngleRad)).toFloat(),
          y = center.y + (radius * sin(thumbAngleRad)).toFloat(),
        )
      drawCircle(color = thumbColor, radius = thumbRadius.toPx(), center = thumbCenter)
    }
    content()
  }
}

@Preview(showBackground = true)
@Composable
private fun CircularSliderPreview() {
  FlashyTheme {
    var v by remember { mutableStateOf(0.4f) }
    CircularSlider(value = v, onValueChange = { v = it }, modifier = Modifier.size(280.dp))
  }
}
