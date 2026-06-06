package com.mtali.flashy2.core.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mtali.flashy2.R
import com.mtali.flashy2.core.ui.theme.FlashyTheme
import com.mtali.flashy2.core.ui.theme.ScreenBlue
import com.mtali.flashy2.core.ui.theme.ScreenGreen
import com.mtali.flashy2.core.ui.theme.ScreenRed
import com.mtali.flashy2.core.ui.theme.ScreenWhite

val ScreenLightPresets = listOf(ScreenWhite, ScreenRed, ScreenGreen, ScreenBlue)

/** A row of preset color swatches plus a palette button that opens the custom color picker. */
@Composable
fun ColorSwatchRow(
  selected: Color,
  onSelect: (Color) -> Unit,
  onCustomClick: () -> Unit,
  modifier: Modifier = Modifier,
  presets: List<Color> = ScreenLightPresets,
) {
  Row(
    modifier = modifier,
    horizontalArrangement = Arrangement.spacedBy(16.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    presets.forEach { color ->
      val isSelected = color == selected
      val diameter by animateDpAsState(if (isSelected) 36.dp else 24.dp, label = "swatch")
      Box(
        modifier =
        Modifier
          .size(40.dp),
        contentAlignment = Alignment.Center,
      ) {
        Box(
          modifier =
          Modifier
            .size(diameter)
            .clip(CircleShape)
            .background(color)
            .border(1.dp, Color.Black.copy(alpha = 0.12f), CircleShape)
            .clickable { onSelect(color) },
        )
      }
    }
    Box(
      modifier =
      Modifier
        .size(40.dp)
        .clip(CircleShape)
        .clickable { onCustomClick() },
      contentAlignment = Alignment.Center,
    ) {
      Icon(
        painter = painterResource(R.drawable.palette),
        contentDescription = stringResource(R.string.cd_color_picker),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.size(26.dp),
      )
    }
  }
}

@Preview(showBackground = true)
@Composable
private fun ColorSwatchRowPreview() {
  FlashyTheme {
    ColorSwatchRow(selected = ScreenRed, onSelect = {}, onCustomClick = {})
  }
}
