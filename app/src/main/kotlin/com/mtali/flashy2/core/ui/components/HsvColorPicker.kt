package com.mtali.flashy2.core.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mtali.flashy2.core.ui.theme.FlashyTheme

private val hueSpectrum =
  listOf(
    Color.Red,
    Color.Yellow,
    Color.Green,
    Color.Cyan,
    Color.Blue,
    Color.Magenta,
    Color.Red,
  )

/**
 * A self-contained HSV color picker (saturation/value square + hue slider), replacing the
 * third-party `com.github.skydoves:colorpickerview`. Emits the selected [Color] via [onColorChange].
 */
@Composable
fun HsvColorPicker(
  initialColor: Color,
  onColorChange: (Color) -> Unit,
  modifier: Modifier = Modifier,
) {
  val initialHsv = remember(initialColor) { initialColor.toHsv() }
  var hue by remember { mutableFloatStateOf(initialHsv[0]) }
  var saturation by remember { mutableFloatStateOf(initialHsv[1]) }
  var value by remember { mutableFloatStateOf(initialHsv[2]) }

  fun emit() = onColorChange(Color.hsv(hue, saturation, value))

  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
    // Saturation / Value square
    Box(
      modifier =
      Modifier
        .fillMaxWidth()
        .aspectRatio(1.4f)
        .clip(RoundedCornerShape(16.dp)),
    ) {
      Box(
        modifier =
        Modifier
          .matchParentSize()
          .background(Brush.horizontalGradient(listOf(Color.White, Color.hsv(hue, 1f, 1f))))
          .background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black)))
          .pointerInput(Unit) {
            fun update(pos: Offset) {
              saturation = (pos.x / size.width).coerceIn(0f, 1f)
              value = (1f - pos.y / size.height).coerceIn(0f, 1f)
              emit()
            }
            detectDragGestures(onDragStart = { update(it) }) { change, _ -> update(change.position) }
          },
      )
      Canvas(modifier = Modifier.matchParentSize()) {
        val cx = saturation * size.width
        val cy = (1f - value) * size.height
        drawCircle(Color.White, radius = 10.dp.toPx(), center = Offset(cx, cy), style = Stroke(width = 3.dp.toPx()))
      }
    }

    // Hue slider
    Box(
      modifier =
      Modifier
        .fillMaxWidth()
        .height(28.dp)
        .clip(RoundedCornerShape(14.dp))
        .background(Brush.horizontalGradient(hueSpectrum))
        .pointerInput(Unit) {
          fun update(pos: Offset) {
            hue = (pos.x / size.width).coerceIn(0f, 1f) * 360f
            emit()
          }
          detectDragGestures(onDragStart = { update(it) }) { change, _ -> update(change.position) }
        },
    ) {
      Canvas(modifier = Modifier.matchParentSize()) {
        val x = (hue / 360f) * size.width
        drawLine(
          color = Color.White,
          start = Offset(x, 0f),
          end = Offset(x, size.height),
          strokeWidth = 4.dp.toPx(),
          cap = StrokeCap.Round,
        )
      }
    }
  }
}

private fun Color.toHsv(): FloatArray {
  val hsv = FloatArray(3)
  android.graphics.Color.colorToHSV(this.toArgb(), hsv)
  return hsv
}

@Preview(showBackground = true)
@Composable
private fun HsvColorPickerPreview() {
  FlashyTheme {
    HsvColorPicker(initialColor = Color(0xFF3A86F7), onColorChange = {}, modifier = Modifier.fillMaxWidth())
  }
}
